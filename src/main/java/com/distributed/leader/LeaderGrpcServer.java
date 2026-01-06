package com.distributed.leader;

import com.distributed.grpc.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class LeaderGrpcServer extends MemberServiceGrpc.MemberServiceImplBase {

    private final MemberManager memberManager;
    private Server server;
    private final int port;

    public LeaderGrpcServer(MemberManager memberManager, int port) {
        this.memberManager = memberManager;
        this.port = port;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port).addService(this).build().start();
        System.out.println("[LEADER-GRPC] Port: " + port);
    }

    public void stop() {
        if (server != null) server.shutdown();
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        boolean success = memberManager.registerMember(request.getMemberId(), request.getHost(), request.getPort());
        responseObserver.onNext(RegisterResponse.newBuilder()
                .setSuccess(success)
                .setMessage(success ? "OK" : "FAIL")
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void unregister(UnregisterRequest request, StreamObserver<UnregisterResponse> responseObserver) {
        boolean success = memberManager.unregisterMember(request.getMemberId());
        responseObserver.onNext(UnregisterResponse.newBuilder().setSuccess(success).build());
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
    public void storeMessage(StoreMessageRequest request, StreamObserver<StoreMessageResponse> responseObserver) {
        responseObserver.onNext(StoreMessageResponse.newBuilder().setSuccess(false).build());
        responseObserver.onCompleted();
    }

    @Override
    public void retrieveMessage(RetrieveMessageRequest request, StreamObserver<RetrieveMessageResponse> responseObserver) {
        responseObserver.onNext(RetrieveMessageResponse.newBuilder().setFound(false).build());
        responseObserver.onCompleted();
    }
}
