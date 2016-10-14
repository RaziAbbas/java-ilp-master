package org.interledger.ilp.ledger.api.handlers;

// TESTING FROM COMMAND LINE: https://blogs.oracle.com/PavelBucek/entry/websocket_command_line_client
// 
//import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.ServerWebSocket;
//import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.http.HttpMethod.GET;

//import javax.money.CurrencyUnit;
//import javax.money.Monetary;
//import javax.money.MonetaryAmount;

//import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
//import org.interledger.ilp.core.AccountUri;
//import org.interledger.ilp.core.ConditionURI;
//import org.interledger.ilp.core.DTTM;
//import org.interledger.ilp.core.LedgerInfo;
//import org.interledger.ilp.core.LedgerTransfer;
//import org.interledger.ilp.core.TransferID;
//import org.interledger.ilp.core.TransferStatus;
//import org.interledger.ilp.ledger.LedgerFactory;
//import org.interledger.ilp.ledger.impl.SimpleLedgerTransfer;
//import org.interledger.ilp.ledger.impl.SimpleLedgerTransferManager;
//import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
//import io.vertx.core.buffer.Buffer;

//import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * @author earizon
 * TransferWSEventHandler handler
 * Wrapper to HTTP GET request to upgrade it to WebSocketEventHandler 
 * ILP-Connector five-bells-plugins will connect to a URL similar to:
 * /accounts/alice/transfers
 * This (GET) request will be upgraded to a webSocket connection in order
 * to send back internal events (transfer accepted, rejected, ...)
 * 
 * Internal java-ilp-ledger components will inform of events to this Handler
 * using a code similar to:
 * 
 *     String wsID = TransferWSEventHandler.getServerWebSocketHandlerID(ilpConnectorIP);
 *     context.vertx().eventBus().send(wsID, "PUT transferID:"+transferID.transferID);
 */
// FIXME: implements ProtectedResource required? 
//    Note: earizon: I didn't find an easy way to add authentication to the connecting WS "client"
public class TransferWSEventHandler extends RestEndpointHandler/* implements ProtectedResource */ {

    private static final Logger log = LoggerFactory.getLogger(TransferWSEventHandler.class);

    /*
     *  FIXME: Change mapping
     *       "ilpConnectorIP" <-> "WebSocket Handler ID"
     *    to
     *              "account" <-> "WebSocket Handler ID"
     *         Chang also:
     *       notifyILPConnector(RoutingContext context, String message)
     *    to
     *       notifyILPConnector(LedgerAccount[] affectedAccounts, String message)
     */
    private static Map<String /*ilpConnector remote IP*/, String /*ws ServerID*/> server2WSHandlerID =
        new HashMap<String, String>();

    public TransferWSEventHandler() {
        super("transfer", "accounts/:account_name/transfers");
        accept(GET);
    }

    public static TransferWSEventHandler create() {
        return new TransferWSEventHandler(); // TODO: return singleton?
    }

    @Override
    protected void handleGet(RoutingContext context) {
        // GET /accounts/alice/transfers -> Upgrade to websocket
        log.debug("TransferWSEventHandler Connected. Upgrading HTTP GET to WebSocket!");
        ServerWebSocket sws = context.request().upgrade();
        /*
         * When a Websocket is created it automatically registers an event 
         * handler with the event bus - the ID of that handler is given by 
         * this method. Given this ID, a different event loop can send a 
         * binary frame to that event handler using the event bus and that 
         * buffer will be received by this instance in its own event loop 
         * and written to the underlying connection. This allows you to 
         * write data to other WebSockets which are owned by different event loops.
         */
        sws.writeFinalTextFrame("Connected!");
        registerServerWebSocket(sws);
    }

    private static void registerServerWebSocket(ServerWebSocket sws) {
        String ilpConnectorIP = sws.remoteAddress().host();
        String handlerID = sws.textHandlerID(); // | binaryHandlerID

        server2WSHandlerID.put(ilpConnectorIP, handlerID);
        sws.frameHandler/* bytes read from the connector */(/*WebSocketFrame*/frame -> {
          log.debug("ilpConnector input frame -> frame.textData()   " + frame.textData());
          log.debug("ilpConnector input frame -> frame.binaryData() " + frame.binaryData());
        });
//      if (bCloseSocket) { websocket.close(); } 

        sws.closeHandler(new Handler<Void>() {
            @Override
            public void handle(final Void event) {
                log.info("un-registering connection from ilp Server: '" + ilpConnectorIP+"'");
                server2WSHandlerID.remove(ilpConnectorIP);
            }
        });
        
//        sws.handler/*data sent from the internal vertX components through the event-Bus */(new Handler<Buffer>() {
//            @Override
//            public void handle(final Buffer data) {
//                String sData = data.toString();
//                log.debug("received '"+sData+"' from internal *Manager:");
//                sws.writeFinalTextFrame(sData);
//                log.debug("message forwarded to ilp connector through websocket");
//            }
//        });
        
        
    }
    
//    public static String getServerWebSocketHandlerID(String connectorIP) {
//        if (!server2WSHandlerID.containsKey(connectorIP)) {
//            throw new RuntimeException("No ws connection exists to ilp-connector "+connectorIP);
//        }
//        return server2WSHandlerID.get(connectorIP);
//    }

    /**
     * Send transacction status update to the ILP connector   
     * @param context
     * @param message
     */
    public static void notifyILPConnector(RoutingContext context, String message) {
        // Send notification to all existing webSockets
        for (String key : server2WSHandlerID.keySet() ) {
            String wsID = server2WSHandlerID.get(key);
            context.vertx().eventBus().send(wsID, message);
        }
    }


}
