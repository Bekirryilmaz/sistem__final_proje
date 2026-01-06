package com.distributed.leader;

import com.distributed.common.ConfigManager;
import com.distributed.common.MemberInfo;
import com.distributed.common.ProtocolConstants;
import com.distributed.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MemberManager {

    private final ConcurrentHashMap<String, MemberInfo> members;
    private final ConcurrentHashMap<String, ManagedChannel> channels;
    private final ConcurrentHashMap<String, MemberServiceGrpc.MemberServiceBlockingStub> stubs;
    private final ConcurrentHashMap<String, Set<String>> messageMetadata;
    private final AtomicLong roundRobinCounter;
    private final ConfigManager configManager;
    private final ExecutorService asyncExecutor;

    public MemberManager(ConfigManager configManager) {
        this.members = new ConcurrentHashMap<>();
        this.channels = new ConcurrentHashMap<>();
        this.stubs = new ConcurrentHashMap<>();
        this.messageMetadata = new ConcurrentHashMap<>();
        this.roundRobinCounter = new AtomicLong(0);
        this.configManager = configManager;
        this.asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    public synchronized boolean registerMember(String memberId, String host, int port) {
        if (members.containsKey(memberId)) {
            members.get(memberId).setAlive(true);
            return true;
        }

        MemberInfo memberInfo = new MemberInfo(memberId, host, port);
        members.put(memberId, memberInfo);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .maxInboundMessageSize(16 * 1024 * 1024)
                .build();
        channels.put(memberId, channel);
        stubs.put(memberId, MemberServiceGrpc.newBlockingStub(channel));

        System.out.println("[LEADER] Üye kaydedildi: " + memberInfo);
        return true;
    }

    public synchronized boolean unregisterMember(String memberId) {
        MemberInfo member = members.remove(memberId);
        if (member == null) return false;

        ManagedChannel channel = channels.remove(memberId);
        if (channel != null) channel.shutdown();
        stubs.remove(memberId);
        return true;
    }

    public void markMemberDead(String memberId) {
        MemberInfo member = members.get(memberId);
        if (member != null) member.setAlive(false);
    }

    public int getAliveCount() {
        int count = 0;
        for (MemberInfo m : members.values()) {
            if (m.isAlive()) count++;
        }
        return count;
    }

    public int getTotalMemberCount() {
        return members.size();
    }

    public List<String> selectMembersForMessage(int count) {
        List<MemberInfo> aliveMembers = new ArrayList<>();
        for (MemberInfo m : members.values()) {
            if (m.isAlive()) aliveMembers.add(m);
        }
        if (aliveMembers.size() < count) return Collections.emptyList();

        aliveMembers.sort(Comparator.comparing(MemberInfo::getMemberId));
        List<String> selected = new ArrayList<>();
        int size = aliveMembers.size();
        long startIndex = roundRobinCounter.getAndIncrement() % size;

        for (int i = 0; i < count; i++) {
            selected.add(aliveMembers.get((int) ((startIndex + i) % size)).getMemberId());
        }
        return selected;
    }

    public boolean storeMessageToMembersAsync(String messageId, String messageContent, List<String> memberIds) {
        int requiredSuccess = memberIds.size();
        AtomicInteger successCount = new AtomicInteger(0);
        Set<String> successfulMembers = ConcurrentHashMap.newKeySet();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String memberId : memberIds) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                MemberServiceGrpc.MemberServiceBlockingStub stub = stubs.get(memberId);
                if (stub == null) return;

                try {
                    StoreMessageRequest request = StoreMessageRequest.newBuilder()
                            .setMessageId(messageId)
                            .setMessageContent(messageContent)
                            .build();

                    StoreMessageResponse response = stub
                            .withDeadlineAfter(ProtocolConstants.GRPC_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .storeMessage(request);

                    if (response.getSuccess()) {
                        successCount.incrementAndGet();
                        successfulMembers.add(memberId);
                        MemberInfo member = members.get(memberId);
                        if (member != null) member.incrementMessageCount();
                    }
                } catch (StatusRuntimeException e) {
                    markMemberDead(memberId);
                }
            }, asyncExecutor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        if (successCount.get() >= requiredSuccess) {
            messageMetadata.put(messageId, successfulMembers);
            return true;
        }
        return false;
    }

    public String retrieveMessage(String messageId) {
        Set<String> holdingMembers = messageMetadata.get(messageId);
        if (holdingMembers == null) return null;

        for (String memberId : holdingMembers) {
            MemberInfo member = members.get(memberId);
            if (member == null || !member.isAlive()) continue;

            MemberServiceGrpc.MemberServiceBlockingStub stub = stubs.get(memberId);
            if (stub == null) continue;

            try {
                RetrieveMessageRequest request = RetrieveMessageRequest.newBuilder()
                        .setMessageId(messageId)
                        .build();

                RetrieveMessageResponse response = stub
                        .withDeadlineAfter(ProtocolConstants.GRPC_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                        .retrieveMessage(request);

                if (response.getFound()) return response.getMessageContent();
            } catch (StatusRuntimeException e) {
                markMemberDead(memberId);
            }
        }
        return null;
    }

    public int getTotalMessageCount() {
        return messageMetadata.size();
    }

    public Collection<MemberInfo> getAllMembers() {
        return members.values();
    }

    public void shutdown() {
        asyncExecutor.shutdown();
        try {
            asyncExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
        }
        for (ManagedChannel channel : channels.values()) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                channel.shutdownNow();
            }
        }
    }
}
