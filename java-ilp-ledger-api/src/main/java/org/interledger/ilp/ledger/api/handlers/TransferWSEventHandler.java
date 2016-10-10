package org.interledger.ilp.ledger.api.handlers;

// TESTING FROM COMMAND LINE: https://blogs.oracle.com/PavelBucek/entry/websocket_command_line_client
// 
//import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.http.HttpMethod.GET;

//import javax.money.CurrencyUnit;
//import javax.money.Monetary;
//import javax.money.MonetaryAmount;

import org.interledger.ilp.common.api.ProtectedResource;
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
import java.util.HashMap;
import java.util.Map;
/**
 * Health handler
 *
 * @author earizon
 */
// FIXME: implements ProtectedResource required? 
//    Note: earizon: I didn't find an easy way to add authentication to the connecting WS "client"
public class TransferWSEventHandler extends RestEndpointHandler/* implements ProtectedResource */ {

    private static final Logger log = LoggerFactory.getLogger(TransferWSEventHandler.class);
    private final static String PARAM_ACCOUNT = "account";

	// GET /accounts/alice/transfers -> Upgrade to websocket
    
    public TransferWSEventHandler() {
        // super("transfer", "accounts/alice/transfers");
        super("transfer", "accounts/:" + PARAM_ACCOUNT + "/transfers");
        accept(GET);
    }

    public static TransferWSEventHandler create() {
        return new TransferWSEventHandler(); // TODO: return singleton?
    }

    @Override
    protected void handleGet(RoutingContext context) {
        log.debug("TransferWSEventHandler Connected!");
        System.out.println("TransferWSEventHandler Connected!");
        // Upgrade to websocket
        ServerWebSocket websocket = context.request().upgrade();
        // Reading frames from WebSockets
        websocket.frameHandler(frame -> {
          System.out.println("TransferWSEventHandler eceived a frame of size!");
//          if (bCloseSocket) { websocket.close(); } 
        });
    }

}
