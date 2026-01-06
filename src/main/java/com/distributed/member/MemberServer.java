package com.distributed.member;

import com.distributed.common.ProtocolConstants;
import com.distributed.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemberServer {

    private final String memberId;
    private final String memberHost;
    private final int memberPort;
    private final String leaderHost;
    private final int leaderGrpcPort;
    private final String localIP;
    private final DiskManager diskManager;
    private final MemberGrpcServer grpcServer;
    private final ScheduledExecutorService logScheduler;
    private ManagedChannel leaderChannel;
    private MemberServiceGrpc.MemberServiceBlockingStub leaderStub;

    public MemberServer(String memberId, String memberHost, int memberPort, String leaderHost, int leaderGrpcPort, DiskManager.WriteStrategy diskStrategy) {
        this.memberId = memberId;
        this.memberHost = memberHost;
        this.memberPort = memberPort;
        this.leaderHost = leaderHost;
        this.leaderGrpcPort = leaderGrpcPort;
        this.localIP = ProtocolConstants.getLocalIP();
        this.diskManager = new DiskManager(memberId, diskStrategy);
        this.grpcServer = new MemberGrpcServer(diskManager, memberPort);
        this.logScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() throws IOException, InterruptedException {
        grpcServer.start();
        connectToLeader();
        registerWithLeader();
        startPeriodicLogging();
        printFamilyHeader();
        grpcServer.blockUntilShutdown();
    }

    private void printFamilyHeader() {
        System.out.println(ProtocolConstants.DOUBLE_LINE);
        System.out.println("Family at " + localIP + ":" + memberPort + " (me)");
        System.out.println("Time: " + ProtocolConstants.getTimestamp());
        System.out.println(ProtocolConstants.DOUBLE_LINE);
        System.out.println("Role: MEMBER");
        System.out.println("Member ID: " + memberId);
        System.out.println("gRPC Port: " + memberPort);
        System.out.println("Leader: " + leaderHost + ":" + leaderGrpcPort);
        System.out.println("Storage: " + diskManager.getStorageDirectory());
        System.out.println("Disk Strategy: " + diskManager.getWriteStrategy());
        System.out.println(ProtocolConstants.SEPARATOR_LINE);
        System.out.println("Ready to receive messages from leader...");
        System.out.println(ProtocolConstants.DOUBLE_LINE);
    }

    private void connectToLeader() {
        leaderChannel = ManagedChannelBuilder.forAddress(leaderHost, leaderGrpcPort)
                .usePlaintext()
                .maxInboundMessageSize(16 * 1024 * 1024)
                .build();
        leaderStub = MemberServiceGrpc.newBlockingStub(leaderChannel);
    }

    private void registerWithLeader() {
        try {
            RegisterRequest request = RegisterRequest.newBuilder()
                    .setMemberId(memberId)
                    .setHost(memberHost)
                    .setPort(memberPort)
                    .build();
            RegisterResponse response = leaderStub
                    .withDeadlineAfter(ProtocolConstants.GRPC_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .register(request);
            System.out.println("[MEMBER] Kayıt: " + (response.getSuccess() ? "OK" : "FAIL"));
        } catch (StatusRuntimeException e) {
            System.err.println("[MEMBER] Kayıt hatası: " + e.getMessage());
        }
    }

    private void unregisterFromLeader() {
        try {
            leaderStub.withDeadlineAfter(ProtocolConstants.GRPC_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .unregister(UnregisterRequest.newBuilder().setMemberId(memberId).build());
        } catch (StatusRuntimeException ignored) {}
    }

    private void startPeriodicLogging() {
        logScheduler.scheduleAtFixedRate(this::printStats,
                ProtocolConstants.LOG_PERIOD_MS, ProtocolConstants.LOG_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    private void printStats() {
        System.out.println();
        System.out.println(ProtocolConstants.DOUBLE_LINE);
        System.out.println("Family at " + localIP + ":" + memberPort + " (me)");
        System.out.println("Time: " + ProtocolConstants.getTimestamp());
        System.out.println(ProtocolConstants.SEPARATOR_LINE);
        System.out.println("Member ID: " + memberId);
        System.out.println("Messages on disk: " + diskManager.getMessageCount());
        System.out.println(ProtocolConstants.DOUBLE_LINE);
    }

    public void stop() {
        if (leaderStub != null) unregisterFromLeader();
        if (leaderChannel != null) {
            try { leaderChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS); }
            catch (InterruptedException e) { leaderChannel.shutdownNow(); }
        }
        grpcServer.stop();
        logScheduler.shutdown();
    }

    public static void main(String[] args) {
        String memberId = "member_" + System.currentTimeMillis();
        String memberHost = "localhost";
        int memberPort = ProtocolConstants.DEFAULT_MEMBER_BASE_PORT;
        String leaderHost = "localhost";
        int leaderGrpcPort = ProtocolConstants.DEFAULT_LEADER_GRPC_PORT;
        DiskManager.WriteStrategy diskStrategy = DiskManager.WriteStrategy.ZERO_COPY;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--id": memberId = args[++i]; break;
                case "--host": memberHost = args[++i]; break;
                case "--port": memberPort = Integer.parseInt(args[++i]); break;
                case "--leader-host": leaderHost = args[++i]; break;
                case "--leader-port": leaderGrpcPort = Integer.parseInt(args[++i]); break;
                case "--disk-strategy":
                    String strategyArg = args[++i].toUpperCase();
                    if ("BUFFERED".equals(strategyArg)) {
                        diskStrategy = DiskManager.WriteStrategy.BUFFERED;
                    } else {
                        diskStrategy = DiskManager.WriteStrategy.ZERO_COPY;
                    }
                    break;
            }
        }

        MemberServer server = new MemberServer(memberId, memberHost, memberPort, leaderHost, leaderGrpcPort, diskStrategy);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        try {
            server.start();
        } catch (IOException | InterruptedException e) {
            System.err.println("[MEMBER] Başlatma hatası: " + e.getMessage());
        }
    }
}
