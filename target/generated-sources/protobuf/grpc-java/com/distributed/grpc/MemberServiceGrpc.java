package com.distributed.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: message_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class MemberServiceGrpc {

  private MemberServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "messageservice.MemberService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.distributed.grpc.RegisterRequest,
      com.distributed.grpc.RegisterResponse> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Register",
      requestType = com.distributed.grpc.RegisterRequest.class,
      responseType = com.distributed.grpc.RegisterResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.distributed.grpc.RegisterRequest,
      com.distributed.grpc.RegisterResponse> getRegisterMethod() {
    io.grpc.MethodDescriptor<com.distributed.grpc.RegisterRequest, com.distributed.grpc.RegisterResponse> getRegisterMethod;
    if ((getRegisterMethod = MemberServiceGrpc.getRegisterMethod) == null) {
      synchronized (MemberServiceGrpc.class) {
        if ((getRegisterMethod = MemberServiceGrpc.getRegisterMethod) == null) {
          MemberServiceGrpc.getRegisterMethod = getRegisterMethod =
              io.grpc.MethodDescriptor.<com.distributed.grpc.RegisterRequest, com.distributed.grpc.RegisterResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.RegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.RegisterResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MemberServiceMethodDescriptorSupplier("Register"))
              .build();
        }
      }
    }
    return getRegisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.distributed.grpc.UnregisterRequest,
      com.distributed.grpc.UnregisterResponse> getUnregisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Unregister",
      requestType = com.distributed.grpc.UnregisterRequest.class,
      responseType = com.distributed.grpc.UnregisterResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.distributed.grpc.UnregisterRequest,
      com.distributed.grpc.UnregisterResponse> getUnregisterMethod() {
    io.grpc.MethodDescriptor<com.distributed.grpc.UnregisterRequest, com.distributed.grpc.UnregisterResponse> getUnregisterMethod;
    if ((getUnregisterMethod = MemberServiceGrpc.getUnregisterMethod) == null) {
      synchronized (MemberServiceGrpc.class) {
        if ((getUnregisterMethod = MemberServiceGrpc.getUnregisterMethod) == null) {
          MemberServiceGrpc.getUnregisterMethod = getUnregisterMethod =
              io.grpc.MethodDescriptor.<com.distributed.grpc.UnregisterRequest, com.distributed.grpc.UnregisterResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Unregister"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.UnregisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.UnregisterResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MemberServiceMethodDescriptorSupplier("Unregister"))
              .build();
        }
      }
    }
    return getUnregisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.distributed.grpc.StoreMessageRequest,
      com.distributed.grpc.StoreMessageResponse> getStoreMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StoreMessage",
      requestType = com.distributed.grpc.StoreMessageRequest.class,
      responseType = com.distributed.grpc.StoreMessageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.distributed.grpc.StoreMessageRequest,
      com.distributed.grpc.StoreMessageResponse> getStoreMessageMethod() {
    io.grpc.MethodDescriptor<com.distributed.grpc.StoreMessageRequest, com.distributed.grpc.StoreMessageResponse> getStoreMessageMethod;
    if ((getStoreMessageMethod = MemberServiceGrpc.getStoreMessageMethod) == null) {
      synchronized (MemberServiceGrpc.class) {
        if ((getStoreMessageMethod = MemberServiceGrpc.getStoreMessageMethod) == null) {
          MemberServiceGrpc.getStoreMessageMethod = getStoreMessageMethod =
              io.grpc.MethodDescriptor.<com.distributed.grpc.StoreMessageRequest, com.distributed.grpc.StoreMessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StoreMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.StoreMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.StoreMessageResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MemberServiceMethodDescriptorSupplier("StoreMessage"))
              .build();
        }
      }
    }
    return getStoreMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.distributed.grpc.RetrieveMessageRequest,
      com.distributed.grpc.RetrieveMessageResponse> getRetrieveMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RetrieveMessage",
      requestType = com.distributed.grpc.RetrieveMessageRequest.class,
      responseType = com.distributed.grpc.RetrieveMessageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.distributed.grpc.RetrieveMessageRequest,
      com.distributed.grpc.RetrieveMessageResponse> getRetrieveMessageMethod() {
    io.grpc.MethodDescriptor<com.distributed.grpc.RetrieveMessageRequest, com.distributed.grpc.RetrieveMessageResponse> getRetrieveMessageMethod;
    if ((getRetrieveMessageMethod = MemberServiceGrpc.getRetrieveMessageMethod) == null) {
      synchronized (MemberServiceGrpc.class) {
        if ((getRetrieveMessageMethod = MemberServiceGrpc.getRetrieveMessageMethod) == null) {
          MemberServiceGrpc.getRetrieveMessageMethod = getRetrieveMessageMethod =
              io.grpc.MethodDescriptor.<com.distributed.grpc.RetrieveMessageRequest, com.distributed.grpc.RetrieveMessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RetrieveMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.RetrieveMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.RetrieveMessageResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MemberServiceMethodDescriptorSupplier("RetrieveMessage"))
              .build();
        }
      }
    }
    return getRetrieveMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.distributed.grpc.HeartbeatRequest,
      com.distributed.grpc.HeartbeatResponse> getHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Heartbeat",
      requestType = com.distributed.grpc.HeartbeatRequest.class,
      responseType = com.distributed.grpc.HeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.distributed.grpc.HeartbeatRequest,
      com.distributed.grpc.HeartbeatResponse> getHeartbeatMethod() {
    io.grpc.MethodDescriptor<com.distributed.grpc.HeartbeatRequest, com.distributed.grpc.HeartbeatResponse> getHeartbeatMethod;
    if ((getHeartbeatMethod = MemberServiceGrpc.getHeartbeatMethod) == null) {
      synchronized (MemberServiceGrpc.class) {
        if ((getHeartbeatMethod = MemberServiceGrpc.getHeartbeatMethod) == null) {
          MemberServiceGrpc.getHeartbeatMethod = getHeartbeatMethod =
              io.grpc.MethodDescriptor.<com.distributed.grpc.HeartbeatRequest, com.distributed.grpc.HeartbeatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Heartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.HeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.distributed.grpc.HeartbeatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MemberServiceMethodDescriptorSupplier("Heartbeat"))
              .build();
        }
      }
    }
    return getHeartbeatMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MemberServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MemberServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MemberServiceStub>() {
        @java.lang.Override
        public MemberServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MemberServiceStub(channel, callOptions);
        }
      };
    return MemberServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MemberServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MemberServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MemberServiceBlockingStub>() {
        @java.lang.Override
        public MemberServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MemberServiceBlockingStub(channel, callOptions);
        }
      };
    return MemberServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MemberServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MemberServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MemberServiceFutureStub>() {
        @java.lang.Override
        public MemberServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MemberServiceFutureStub(channel, callOptions);
        }
      };
    return MemberServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void register(com.distributed.grpc.RegisterRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.RegisterResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    /**
     */
    default void unregister(com.distributed.grpc.UnregisterRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.UnregisterResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUnregisterMethod(), responseObserver);
    }

    /**
     */
    default void storeMessage(com.distributed.grpc.StoreMessageRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.StoreMessageResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStoreMessageMethod(), responseObserver);
    }

    /**
     */
    default void retrieveMessage(com.distributed.grpc.RetrieveMessageRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.RetrieveMessageResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRetrieveMessageMethod(), responseObserver);
    }

    /**
     */
    default void heartbeat(com.distributed.grpc.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.HeartbeatResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartbeatMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service MemberService.
   */
  public static abstract class MemberServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return MemberServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service MemberService.
   */
  public static final class MemberServiceStub
      extends io.grpc.stub.AbstractAsyncStub<MemberServiceStub> {
    private MemberServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MemberServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MemberServiceStub(channel, callOptions);
    }

    /**
     */
    public void register(com.distributed.grpc.RegisterRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.RegisterResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void unregister(com.distributed.grpc.UnregisterRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.UnregisterResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUnregisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void storeMessage(com.distributed.grpc.StoreMessageRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.StoreMessageResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStoreMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void retrieveMessage(com.distributed.grpc.RetrieveMessageRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.RetrieveMessageResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRetrieveMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void heartbeat(com.distributed.grpc.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.distributed.grpc.HeartbeatResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service MemberService.
   */
  public static final class MemberServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<MemberServiceBlockingStub> {
    private MemberServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MemberServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MemberServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.distributed.grpc.RegisterResponse register(com.distributed.grpc.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.distributed.grpc.UnregisterResponse unregister(com.distributed.grpc.UnregisterRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUnregisterMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.distributed.grpc.StoreMessageResponse storeMessage(com.distributed.grpc.StoreMessageRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStoreMessageMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.distributed.grpc.RetrieveMessageResponse retrieveMessage(com.distributed.grpc.RetrieveMessageRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRetrieveMessageMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.distributed.grpc.HeartbeatResponse heartbeat(com.distributed.grpc.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service MemberService.
   */
  public static final class MemberServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<MemberServiceFutureStub> {
    private MemberServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MemberServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MemberServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.distributed.grpc.RegisterResponse> register(
        com.distributed.grpc.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.distributed.grpc.UnregisterResponse> unregister(
        com.distributed.grpc.UnregisterRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUnregisterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.distributed.grpc.StoreMessageResponse> storeMessage(
        com.distributed.grpc.StoreMessageRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStoreMessageMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.distributed.grpc.RetrieveMessageResponse> retrieveMessage(
        com.distributed.grpc.RetrieveMessageRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRetrieveMessageMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.distributed.grpc.HeartbeatResponse> heartbeat(
        com.distributed.grpc.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER = 0;
  private static final int METHODID_UNREGISTER = 1;
  private static final int METHODID_STORE_MESSAGE = 2;
  private static final int METHODID_RETRIEVE_MESSAGE = 3;
  private static final int METHODID_HEARTBEAT = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER:
          serviceImpl.register((com.distributed.grpc.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<com.distributed.grpc.RegisterResponse>) responseObserver);
          break;
        case METHODID_UNREGISTER:
          serviceImpl.unregister((com.distributed.grpc.UnregisterRequest) request,
              (io.grpc.stub.StreamObserver<com.distributed.grpc.UnregisterResponse>) responseObserver);
          break;
        case METHODID_STORE_MESSAGE:
          serviceImpl.storeMessage((com.distributed.grpc.StoreMessageRequest) request,
              (io.grpc.stub.StreamObserver<com.distributed.grpc.StoreMessageResponse>) responseObserver);
          break;
        case METHODID_RETRIEVE_MESSAGE:
          serviceImpl.retrieveMessage((com.distributed.grpc.RetrieveMessageRequest) request,
              (io.grpc.stub.StreamObserver<com.distributed.grpc.RetrieveMessageResponse>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((com.distributed.grpc.HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<com.distributed.grpc.HeartbeatResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRegisterMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.distributed.grpc.RegisterRequest,
              com.distributed.grpc.RegisterResponse>(
                service, METHODID_REGISTER)))
        .addMethod(
          getUnregisterMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.distributed.grpc.UnregisterRequest,
              com.distributed.grpc.UnregisterResponse>(
                service, METHODID_UNREGISTER)))
        .addMethod(
          getStoreMessageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.distributed.grpc.StoreMessageRequest,
              com.distributed.grpc.StoreMessageResponse>(
                service, METHODID_STORE_MESSAGE)))
        .addMethod(
          getRetrieveMessageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.distributed.grpc.RetrieveMessageRequest,
              com.distributed.grpc.RetrieveMessageResponse>(
                service, METHODID_RETRIEVE_MESSAGE)))
        .addMethod(
          getHeartbeatMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.distributed.grpc.HeartbeatRequest,
              com.distributed.grpc.HeartbeatResponse>(
                service, METHODID_HEARTBEAT)))
        .build();
  }

  private static abstract class MemberServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MemberServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.distributed.grpc.MessageService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MemberService");
    }
  }

  private static final class MemberServiceFileDescriptorSupplier
      extends MemberServiceBaseDescriptorSupplier {
    MemberServiceFileDescriptorSupplier() {}
  }

  private static final class MemberServiceMethodDescriptorSupplier
      extends MemberServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    MemberServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MemberServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MemberServiceFileDescriptorSupplier())
              .addMethod(getRegisterMethod())
              .addMethod(getUnregisterMethod())
              .addMethod(getStoreMessageMethod())
              .addMethod(getRetrieveMessageMethod())
              .addMethod(getHeartbeatMethod())
              .build();
        }
      }
    }
    return result;
  }
}
