package org.interledger.ilp.grpc;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.0.1)",
    comments = "Source: ilp_protocol.proto")
public class ILProtocolGrpc {

  private ILProtocolGrpc() {}

  public static final String SERVICE_NAME = "networking.ILProtocol";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata,
      org.interledger.ilp.grpc.IlpProtocol.Empty> METHOD_UPDATE_LEDGER_ILPMETADATA =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "networking.ILProtocol", "updateLedgerILPMetadata"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.interledger.ilp.grpc.IlpProtocol.ILPTransfer,
      org.interledger.ilp.grpc.IlpProtocol.Empty> METHOD_SETUP_LEDGER_TRANSFER_EMITTER =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING,
          generateFullMethodName(
              "networking.ILProtocol", "setupLedgerTransferEmitter"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.ILPTransfer.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.interledger.ilp.grpc.IlpProtocol.Empty,
      org.interledger.ilp.grpc.IlpProtocol.ILPTransfer> METHOD_SETUP_LEDGER_TRANSFER_RECEIVER =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "networking.ILProtocol", "setupLedgerTransferReceiver"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.Empty.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.ILPTransfer.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification,
      org.interledger.ilp.grpc.IlpProtocol.Empty> METHOD_SETUP_FULFILLMENT_EMITTER =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING,
          generateFullMethodName(
              "networking.ILProtocol", "setupFulfillmentEmitter"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.interledger.ilp.grpc.IlpProtocol.Empty,
      org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification> METHOD_SETUP_FULFILLMENT_RECEIVER =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "networking.ILProtocol", "setupFulfillmentReceiver"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.Empty.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ILProtocolStub newStub(io.grpc.Channel channel) {
    return new ILProtocolStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ILProtocolBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ILProtocolBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static ILProtocolFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ILProtocolFutureStub(channel);
  }

  /**
   */
  public static abstract class ILProtocolImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * inform connector about ledger metadata
     * </pre>
     */
    public void updateLedgerILPMetadata(org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata request,
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_UPDATE_LEDGER_ILPMETADATA, responseObserver);
    }

    /**
     * <pre>
     * 
     * setup transfer notification channel.
     * Ledger will notify about new ILP transfers (a "buyer" starts an ILP-payment in this ledger),
     * while the connector will reply/notify back about new fulfillments (the remote ledger
     * accepted the payment and fulfilled with a valid signature the crypto-condition)
     * </pre>
     */
    public io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.ILPTransfer> setupLedgerTransferEmitter(
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_SETUP_LEDGER_TRANSFER_EMITTER, responseObserver);
    }

    /**
     */
    public void setupLedgerTransferReceiver(org.interledger.ilp.grpc.IlpProtocol.Empty request,
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.ILPTransfer> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SETUP_LEDGER_TRANSFER_RECEIVER, responseObserver);
    }

    /**
     * <pre>
     * A "seller" account in this ledger will receive ILP-payments. It will compare
     * the crypto-condition and fulfill the execution payment sending back an stream of 
     * FulfillmentNotifications for each accepted payment.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification> setupFulfillmentEmitter(
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_SETUP_FULFILLMENT_EMITTER, responseObserver);
    }

    /**
     */
    public void setupFulfillmentReceiver(org.interledger.ilp.grpc.IlpProtocol.Empty request,
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SETUP_FULFILLMENT_RECEIVER, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_UPDATE_LEDGER_ILPMETADATA,
            asyncUnaryCall(
              new MethodHandlers<
                org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata,
                org.interledger.ilp.grpc.IlpProtocol.Empty>(
                  this, METHODID_UPDATE_LEDGER_ILPMETADATA)))
          .addMethod(
            METHOD_SETUP_LEDGER_TRANSFER_EMITTER,
            asyncClientStreamingCall(
              new MethodHandlers<
                org.interledger.ilp.grpc.IlpProtocol.ILPTransfer,
                org.interledger.ilp.grpc.IlpProtocol.Empty>(
                  this, METHODID_SETUP_LEDGER_TRANSFER_EMITTER)))
          .addMethod(
            METHOD_SETUP_LEDGER_TRANSFER_RECEIVER,
            asyncServerStreamingCall(
              new MethodHandlers<
                org.interledger.ilp.grpc.IlpProtocol.Empty,
                org.interledger.ilp.grpc.IlpProtocol.ILPTransfer>(
                  this, METHODID_SETUP_LEDGER_TRANSFER_RECEIVER)))
          .addMethod(
            METHOD_SETUP_FULFILLMENT_EMITTER,
            asyncClientStreamingCall(
              new MethodHandlers<
                org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification,
                org.interledger.ilp.grpc.IlpProtocol.Empty>(
                  this, METHODID_SETUP_FULFILLMENT_EMITTER)))
          .addMethod(
            METHOD_SETUP_FULFILLMENT_RECEIVER,
            asyncServerStreamingCall(
              new MethodHandlers<
                org.interledger.ilp.grpc.IlpProtocol.Empty,
                org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification>(
                  this, METHODID_SETUP_FULFILLMENT_RECEIVER)))
          .build();
    }
  }

  /**
   */
  public static final class ILProtocolStub extends io.grpc.stub.AbstractStub<ILProtocolStub> {
    private ILProtocolStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ILProtocolStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ILProtocolStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ILProtocolStub(channel, callOptions);
    }

    /**
     * <pre>
     * inform connector about ledger metadata
     * </pre>
     */
    public void updateLedgerILPMetadata(org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata request,
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_UPDATE_LEDGER_ILPMETADATA, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 
     * setup transfer notification channel.
     * Ledger will notify about new ILP transfers (a "buyer" starts an ILP-payment in this ledger),
     * while the connector will reply/notify back about new fulfillments (the remote ledger
     * accepted the payment and fulfilled with a valid signature the crypto-condition)
     * </pre>
     */
    public io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.ILPTransfer> setupLedgerTransferEmitter(
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_SETUP_LEDGER_TRANSFER_EMITTER, getCallOptions()), responseObserver);
    }

    /**
     */
    public void setupLedgerTransferReceiver(org.interledger.ilp.grpc.IlpProtocol.Empty request,
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.ILPTransfer> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_SETUP_LEDGER_TRANSFER_RECEIVER, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * A "seller" account in this ledger will receive ILP-payments. It will compare
     * the crypto-condition and fulfill the execution payment sending back an stream of 
     * FulfillmentNotifications for each accepted payment.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification> setupFulfillmentEmitter(
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_SETUP_FULFILLMENT_EMITTER, getCallOptions()), responseObserver);
    }

    /**
     */
    public void setupFulfillmentReceiver(org.interledger.ilp.grpc.IlpProtocol.Empty request,
        io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_SETUP_FULFILLMENT_RECEIVER, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ILProtocolBlockingStub extends io.grpc.stub.AbstractStub<ILProtocolBlockingStub> {
    private ILProtocolBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ILProtocolBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ILProtocolBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ILProtocolBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * inform connector about ledger metadata
     * </pre>
     */
    public org.interledger.ilp.grpc.IlpProtocol.Empty updateLedgerILPMetadata(org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata request) {
      return blockingUnaryCall(
          getChannel(), METHOD_UPDATE_LEDGER_ILPMETADATA, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.interledger.ilp.grpc.IlpProtocol.ILPTransfer> setupLedgerTransferReceiver(
        org.interledger.ilp.grpc.IlpProtocol.Empty request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_SETUP_LEDGER_TRANSFER_RECEIVER, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification> setupFulfillmentReceiver(
        org.interledger.ilp.grpc.IlpProtocol.Empty request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_SETUP_FULFILLMENT_RECEIVER, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ILProtocolFutureStub extends io.grpc.stub.AbstractStub<ILProtocolFutureStub> {
    private ILProtocolFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ILProtocolFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ILProtocolFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ILProtocolFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * inform connector about ledger metadata
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.interledger.ilp.grpc.IlpProtocol.Empty> updateLedgerILPMetadata(
        org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_UPDATE_LEDGER_ILPMETADATA, getCallOptions()), request);
    }
  }

  private static final int METHODID_UPDATE_LEDGER_ILPMETADATA = 0;
  private static final int METHODID_SETUP_LEDGER_TRANSFER_RECEIVER = 1;
  private static final int METHODID_SETUP_FULFILLMENT_RECEIVER = 2;
  private static final int METHODID_SETUP_LEDGER_TRANSFER_EMITTER = 3;
  private static final int METHODID_SETUP_FULFILLMENT_EMITTER = 4;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ILProtocolImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(ILProtocolImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_UPDATE_LEDGER_ILPMETADATA:
          serviceImpl.updateLedgerILPMetadata((org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata) request,
              (io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty>) responseObserver);
          break;
        case METHODID_SETUP_LEDGER_TRANSFER_RECEIVER:
          serviceImpl.setupLedgerTransferReceiver((org.interledger.ilp.grpc.IlpProtocol.Empty) request,
              (io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.ILPTransfer>) responseObserver);
          break;
        case METHODID_SETUP_FULFILLMENT_RECEIVER:
          serviceImpl.setupFulfillmentReceiver((org.interledger.ilp.grpc.IlpProtocol.Empty) request,
              (io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.FulfillmentNotification>) responseObserver);
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
        case METHODID_SETUP_LEDGER_TRANSFER_EMITTER:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.setupLedgerTransferEmitter(
              (io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty>) responseObserver);
        case METHODID_SETUP_FULFILLMENT_EMITTER:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.setupFulfillmentEmitter(
              (io.grpc.stub.StreamObserver<org.interledger.ilp.grpc.IlpProtocol.Empty>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_UPDATE_LEDGER_ILPMETADATA,
        METHOD_SETUP_LEDGER_TRANSFER_EMITTER,
        METHOD_SETUP_LEDGER_TRANSFER_RECEIVER,
        METHOD_SETUP_FULFILLMENT_EMITTER,
        METHOD_SETUP_FULFILLMENT_RECEIVER);
  }

}
