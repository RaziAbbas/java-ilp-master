package org.interledger.ilp.common.api.handlers;

//import io.netty.handler.codec.http.HttpResponseStatus;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
//import io.vertx.core.json.Json;
//import io.vertx.core.json.JsonArray;
//import io.vertx.core.json.JsonObject;
//import io.netty.handler.codec.http.HttpResponseStatus;
//import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

//import static io.vertx.core.http.HttpMethod.GET;
//import static io.vertx.core.http.HttpMethod.POST;

//import javax.money.CurrencyUnit;
//import javax.money.Monetary;
//import javax.money.MonetaryAmount;

import org.interledger.ilp.common.api.ProtectedResource;
//import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
//import org.interledger.ilp.core.AccountUri;
//import org.interledger.ilp.core.ConditionURI;
//import org.interledger.ilp.core.DTTM;
//import org.interledger.ilp.core.LedgerInfo;
//import org.interledger.ilp.core.LedgerTransfer;
//import org.interledger.ilp.core.TransferID;
//import org.interledger.ilp.core.TransferStatus;
//import org.javamoney.moneta.Money;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Health handler
 *
 * @author earizon
 */
public class WebSocketEventHandler implements ProtectedResource, Handler<ServerWebSocket>  {
    // Each connected ILP connector (actuallly just one for the PoC) will create a new long-running
    // WebSocketEventHandler
    private static Map<String /*server*/, WebSocketEventHandler> mapILPCon2Handler = new HashMap<String, WebSocketEventHandler>();

    // Make default constructor private to avoid external code to create it.
    private WebSocketEventHandler() { }

    public static WebSocketEventHandler getWebSocketEventHandler(String ilpConnectorAddress) {
        if (! mapILPCon2Handler.containsKey(ilpConnectorAddress)) {
            System.out.println("debug:WebSocketEventHandler singleton created "+ilpConnectorAddress);
            mapILPCon2Handler.put(ilpConnectorAddress, new WebSocketEventHandler());
        }
        return  mapILPCon2Handler.get(ilpConnectorAddress);
    }

    @Override
    public void handle(ServerWebSocket websocket) {
        if ( ! websocket.path().endsWith("/transfer")) {
            websocket.reject();
        } else {
           System.out.println("Web Socket Connected!");
        }
    }
}




