package com.distributed.member;

import com.distributed.common.ProtocolConstants;
import com.distributed.grpc.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class MemberGrpcServer extends MemberServiceGrpc.MemberServiceImplBase {

    private final DiskManager diskManager;
    private Server server;
    private final int port;

    public MemberGrpcServer(DiskManager diskManager, int port) {
        this.diskManager = diskManager;
        this.port = port;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port).addService(this).build().start();
        System.out.println("[MEMBER-GRPC] Port: " + port);
    }

    public void stop() {
        if (server != null) server.shutdown();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) server.awaitTermination();
    }

    @Override
    public void storeMessage(StoreMessageRequest request, StreamObserver<StoreMessageResponse> responseObserver) {
        String messageId = request.getMessageId();
        String content = request.getMessageContent();

        System.out.println("[MEMBER] Store: " + messageId);
        boolean success = diskManager.storeMessage(messageId, content);

        responseObserver.onNext(StoreMessageResponse.newBuilder()
                .setSuccess(success)
                .setErrorMessage(success ? "" : "Disk error")
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void retrieveMessage(RetrieveMessageRequest request, StreamObserver<RetrieveMessageResponse> responseObserver) {
        String content = diskManager.retrieveMessage(request.getMessageId());

        RetrieveMessageResponse.Builder builder = RetrieveMessageResponse.newBuilder();
        if (content != null) {
            builder.setFound(true).setMessageContent(content);
        } else {
            builder.setFound(false);
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        responseObserver.onNext(HeartbeatResponse.newBuilder()
                .setAlive(true)
                .setTimestamp(System.currentTimeMillis())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        responseObserver.onNext(RegisterResponse.newBuilder().setSuccess(false).build());
        responseObserver.onCompleted();
    }

    @Override
    public void unregister(UnregisterRequest request, StreamObserver<UnregisterResponse> responseObserver) {
        responseObserver.onNext(UnregisterResponse.newBuilder().setSuccess(false).build());
        responseObserver.onCompleted();
    }
}
