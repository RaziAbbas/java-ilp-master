package com.everis.ilp.ledger.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.interledger.ilp.grpc.ILProtocolGrpc;
import org.interledger.ilp.grpc.IlpProtocol.LedgerMetadata;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class ConnectorGRPCClient {
  private static final Logger logger = Logger.getLogger(ConnectorGRPCClient.class.getName());

  private final ManagedChannel channel;
  private final ILProtocolGrpc.ILProtocolBlockingStub blockingStub;

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public ConnectorGRPCClient(String host, int port) {
    channel = ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext(true)
        .build();
    blockingStub = ILProtocolGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public void setLedgerILPMetadata
      (String ledgerPrefix, String currencyCode, String currencySymbol, int precision, int scale) {
    LedgerMetadata request = LedgerMetadata.newBuilder()
        //.setConnectorList(0, "") // TODO:?
        .setCurrencyCode(currencyCode)
        .setCurrencySymbol(currencySymbol)
        .setPrecision(precision)
        .setPrefix(ledgerPrefix)
        .setScale(scale)
        .build();
    try {
      logger.info("Greeting: " + blockingStub.updateLedgerILPMetadata(request));
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
      ConnectorGRPCClient client = new ConnectorGRPCClient("127.0.0.1", 2222);
      client.setLedgerILPMetadata("ledger1.eur.", "EUR", "€", 10, 2 );
  }
}
