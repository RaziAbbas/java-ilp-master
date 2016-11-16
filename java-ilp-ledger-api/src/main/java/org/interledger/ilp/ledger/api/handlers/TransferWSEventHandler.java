package org.interledger.ilp.ledger.api.handlers;

// TESTING FROM COMMAND LINE: https://blogs.oracle.com/PavelBucek/entry/websocket_command_line_client
// 
//import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import static io.vertx.core.http.HttpMethod.GET;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;

import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author earizon TransferWSEventHandler handler Wrapper to HTTP GET request to
 * upgrade it to WebSocketEventHandler ILP-Connector five-bells-plugins will
 * connect to a URL similar to: /accounts/alice/transfers This (GET) request
 * will be upgraded to a webSocket connection in order to send back internal
 * events (transfer accepted, rejected, ...)
 *
 * Internal java-ilp-ledger components will inform of events to this Handler
 * using a code similar to:
 *
 * String wsID =
 * TransferWSEventHandler.getServerWebSocketHandlerID(ilpConnectorIP);
 * context.vertx().eventBus().send(wsID, "PUT
 * transferID:"+transferID.transferID);
 */
// FIXME: implements ProtectedResource required? 
//    Note: earizon: I didn't find an easy way to add authentication to the connecting WS "client"
public class TransferWSEventHandler extends RestEndpointHandler/* implements ProtectedResource */ {

    private static final Logger log = LoggerFactory.getLogger(TransferWSEventHandler.class);

    private final static String PARAM_NAME = "name";

    /*
     *  FIXME: Change mapping
     *       "ilpConnectorIP" <-> "WebSocket Handler ID"
     *    to
     *              "account" <-> "WebSocket Handler ID"
     *         Change also:
     *       notifyILPConnector(RoutingContext context, String message)
     *    to
     *       notifyILPConnector(RoutingContext context, LedgerAccount[] affectedAccounts, String message)
     */
    private static Map<String /*ilpConnector remote IP*/, String /*ws ServerID*/> server2WSHandlerID
            = new HashMap<String, String>();

    public TransferWSEventHandler() {
        super("transfer", "accounts/:" + PARAM_NAME + "/transfers");
        accept(GET);
    }

    public static TransferWSEventHandler create() {
        return new TransferWSEventHandler(); // TODO: return singleton?
    }

    @Override
    protected void handleGet(RoutingContext context) {
        String accountName = context.request().getParam(PARAM_NAME);
        // GET /accounts/alice/transfers -> Upgrade to websocket
        log.debug("TransferWSEventHandler Connected. Upgrading HTTP GET to WebSocket!");
        ServerWebSocket sws = context.request().upgrade();
        /* 
         * From vertx Docs:
         *    """When a Websocket is created it automatically registers an event 
         *    handler with the event bus - the ID of that handler is given by 
         *    this method. Given this ID, a different event loop can send a 
         *    binary frame to that event handler using the event bus and that 
         *    buffer will be received by this instance in its own event loop 
         *    and written to the underlying connection. This allows you to 
         *    write data to other WebSockets which are owned by different event loops."""
         */
        /* REF:
         * function * subscribeTransfer @ src/controllers/accounts: 
         *   // Send a message upon connection
         *   this.websocket.send(JSON.stringify({ type: 'connect' })) 
         */
        sws.writeFinalTextFrame("{\"type\" : \"connect\" }"); // TODO: recheck this line
        registerServerWebSocket(accountName, sws);
    }

    private static void registerServerWebSocket(String accountName, ServerWebSocket sws) {
        String handlerID = sws.textHandlerID(); // | binaryHandlerID
        log.trace("registering WS connection: "+accountName);

        server2WSHandlerID.put(accountName, handlerID);
        sws.frameHandler/* bytes read from the connector */(/*WebSocketFrame*/frame -> {
                            log.debug("ilpConnector input frame -> frame.textData()   " + frame.textData());
                            log.debug("ilpConnector input frame -> frame.binaryData() " + frame.binaryData());
                        });
//      if (bCloseSocket) { websocket.close(); } 

        sws.closeHandler(new Handler<Void>() {
            @Override
            public void handle(final Void event) {
//                log.info("un-registering connection from ilp Server: '" + ilpConnectorIP + "'");
                log.trace("un-registering WS connection: "+accountName);
                server2WSHandlerID.remove(accountName);
            }
        });

        sws.handler/* @bookmark1 data sent from the internal vertX components through the event-Bus */( /* Handler<Buffer> */ data -> {
            String sData = data.toString();
            log.debug("received '"+sData+"' from internal *Manager:");
            sws.writeFinalTextFrame(sData);
            log.debug("message forwarded to websocket peer through websocket");
        });
    }

//    public static String getServerWebSocketHandlerID(String connectorIP) {
//        if (!server2WSHandlerID.containsKey(connectorIP)) {
//            throw new RuntimeException("No ws connection exists to ilp-connector "+connectorIP);
//        }
//        return server2WSHandlerID.get(connectorIP);
//    }
    /**
     * Send transacction status update to the ILP connector
     *
     * @param context
     * @param message
     */
    public static void notifyListener(RoutingContext context, String account, String message) {
        // Send notification to all existing webSockets
        log.debug("{\n"
                + "notifyListener to account:"+account + ", message: "+message +"\n"
                + "}");
        String wsID = server2WSHandlerID.get(account);

        context.vertx().eventBus().send(wsID, message); // will be sent to handler "@bookmark1"
    }

}
