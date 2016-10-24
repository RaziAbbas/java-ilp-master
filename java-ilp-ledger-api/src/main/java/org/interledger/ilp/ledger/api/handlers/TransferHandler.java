package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;
import static io.vertx.core.http.HttpMethod.PUT;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.auth.impl.SimpleAuthProvider;
import org.interledger.ilp.common.api.core.InterledgerException;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.ConditionURI;
import org.interledger.ilp.core.Credit;
import org.interledger.ilp.core.DTTM;
import org.interledger.ilp.core.Debit;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.core.TransferStatus;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerTransfer;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerTransferManager;
import org.interledger.ilp.ledger.transfer.LedgerTransferManager;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransferHandler handler
 *
 * @author earizon
 * REF: five-bells-ledger/src/controllers/transfers.js
 */
public class TransferHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(TransferHandler.class);
    private final static String transferUUID  = "transferUUID",
                                execCondition = "execCondition";
    // GET|PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 

    public TransferHandler() {
        // REF: https://github.com/interledger/five-bells-ledger/blob/master/src/lib/app.js
        super("transfer", new String[] 
            {
                "transfers/:" + transferUUID,
            });
        accept(GET,POST, PUT);
    }

//    public TransferHandler with(LedgerAccountManager ledgerAccountManager) {
//        this.ledgerAccountManager = ledgerAccountManager;
//        return this;
//    }
    public static TransferHandler create() {
        return new TransferHandler(); // TODO: return singleton?
    }

    @Override
    protected void handlePut(RoutingContext context) {
        SimpleAuthProvider.SimpleUser user = (SimpleAuthProvider.SimpleUser) context.user();
        boolean isAdmin = user.hasRole("admin");
        boolean transferMatchUser = true; // FIXME: TODO: implement
        if (!isAdmin && !transferMatchUser) {
          throw new InterledgerException(InterledgerException.RegisteredException.ForbiddenError);
        }
        log.debug(this.getClass().getName() + "handlePut invoqued ");
        log.debug(context.getBodyAsString());
        /* REQUEST:
         *     PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 HTTP/1.1
         *     Authorization: Basic YWxpY2U6YWxpY2U=
         *     {"id":"http://localhost/transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204",
         *     "ledger":"http://localhost",
         *     "debits":[
         *          {"account":"http://localhost/accounts/alice","amount":"50"},
         *          {"account":"http://localhost/accounts/candice","amount":"20"}],
         *     "credits":[
         *          {"account":"http://localhost/accounts/bob","amount":"30"},
         *          {"account":"http://localhost/accounts/dave","amount":"40"}],
         *          "execution_condition":"cc:0:3:Xk14jPQJs7vrbEZ9FkeZPGBr0YqVzkpOYjFp_tIZMgs:7",
         *     "expires_at":"2015-06-16T00:00:01.000Z",
         *     "state":"prepared"}
         * ANSWER:
         *     HTTP/1.1 201 Created
         *     {"id":"http://localhost/transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204",
         *     "ledger":...,
         *     "debits":[ ... ]
         *     "credits":[ ... ]
         *     "execution_condition":"...",
         *     "expires_at":...,
         *     "state":"proposed",
         *     "timeline":{"proposed_at":"2015-06-16T00:00:00.000Z"}
         *     }
         */
        JsonObject requestBody = getBodyAsJson(context);
        TransferID transferID = new TransferID(context.request().getParam(transferUUID));
        
        // TODO: Check equestBody.getString("ledger") match ledger host/port

//        String state = "proposed"; 
//        if (requestBody.getString("state") != null &&
//            "proposed".equals(requestBody.getString("state")) ) {
//            log.warn("state must be 'proposed' for new transactions");
//        }

        JsonArray debits = requestBody.getJsonArray("debits");
        if (debits.size() > 1) {
            throw new RuntimeException("Transactions from multiple source debits not implemented");
        }
        JsonObject jsonDebit0 = debits.getJsonObject(0);
        // debit0 will be similar to {"account":"http://localhost/accounts/alice","amount":"50"}
        AccountUri fromURI0 = AccountUri.buildFromURI(jsonDebit0.getString("account") /*account URI*/);

        LedgerInfo ledgerInfo = LedgerFactory.getDefaultLedger().getInfo();

        CurrencyUnit currencyUnit /*local ledger currency */ = Monetary.getCurrency(ledgerInfo.getCurrencyCode());
        MonetaryAmount debit0_ammount = Money.of(Double.parseDouble(jsonDebit0.getString("amount")), currencyUnit);
        Debit debit0 = new Debit(fromURI0, debit0_ammount);

        // REF: JsonArray ussage: http://www.programcreek.com/java-api-examples/index.php?api=io.vertx.core.json.JsonArray
        JsonArray credits = requestBody.getJsonArray("credits");
        if (credits.size() > 1) {
            throw new RuntimeException("Transactions to multiple destination credit accounts not implemented");
        }
        JsonObject jsonCredit0 = credits.getJsonObject(0);
//        {"account":"http://localhost/accounts/bob","amount":"30"},
        AccountUri toURI0 = AccountUri.buildFromURI(jsonCredit0.getString("account") /*accountURI */);
        MonetaryAmount credit0_ammount = Money.of(Double.parseDouble(jsonCredit0.getString("amount")), currencyUnit);
        Credit credit0 = new Credit(toURI0, credit0_ammount);

        LedgerTransferManager tm = SimpleLedgerTransferManager.getSingleton();
        // TODO: IMPROVEMENT: isLocalTransaction check and related logic must be done in 
        // LedgerTransferManager using the private isLocalTransaction(LedgerTransfer transfer)
        String data = ""; // Not used
        String noteToSelf = ""; // Not used
        DTTM DTTM_proposed = DTTM.getNow();
        log.debug(transferID.transferID+" expires_at == null" + (requestBody.getString("expires_at") == null));
        DTTM DTTM_expires = requestBody.getString("expires_at") != null
                ? DTTM.c(requestBody.getString("expires_at"))
                : DTTM.future; // TODO: RECHECK
        ConditionURI URIExecutionCond = (requestBody.getString("execution_condition") != null)
                ? ConditionURI.c(requestBody.getString("execution_condition"))
                : ConditionURI.EMPTY ;
        String cancelation_condition = requestBody.getString("cancellation_condition");
                                                              
        ConditionURI URICancelationCond = (cancelation_condition != null)
                ? ConditionURI.c(cancelation_condition)
                : ConditionURI.EMPTY ;
        LedgerTransfer receivedTransfer = new SimpleLedgerTransfer(transferID,
                new Debit[]{debit0}, new Credit[]{credit0},
                URIExecutionCond, URICancelationCond, DTTM_expires, DTTM_proposed,
                data, noteToSelf, TransferStatus.PROPOSED);
        
        boolean isNewTransfer = !tm.transferExists(transferID);
        log.debug(">>> is new transfer?: " + isNewTransfer);
        LedgerTransfer effectiveTransfer = (isNewTransfer) ? receivedTransfer : tm.getTransferById(transferID);
        if (!isNewTransfer) {
            // Check that received json data match existing transaction.
            if (!effectiveTransfer.getCredits()[0].equals(receivedTransfer.getCredits()[0])
                    || !effectiveTransfer.getDebits()[0].equals(receivedTransfer.getDebits()[0])) {
                throw new RuntimeException("data for credits and/or debits doesn't match existing registry");
            }
        } else {
            tm.createNewRemoteILPTransfer(receivedTransfer);
        }
        try {
            String notification = ((SimpleLedgerTransfer) effectiveTransfer).toILPJSONStringifiedFormat();
            log.debug("send transfer update to ILP Connector through websocket: \n:" + notification + "\n");
            TransferWSEventHandler.notifyILPConnector(context,
                    notification);
        } catch (Exception e) {
            log.warn("transaction created correctly but ilp-connector couldn't be notified due to " + e.toString());
            /* FIXME:(improvement) The message must be added to a pool of pending event notifications to 
             *     send to the connector once the (websocket) connection is restablished.
             */
        }
        String response = ((SimpleLedgerTransfer) effectiveTransfer).toWalletJSONStringifiedFormat(false);

        context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .putHeader(HttpHeaders.CONTENT_LENGTH, ""+response.length())
            .setStatusCode(isNewTransfer ? HttpResponseStatus.CREATED.code() : HttpResponseStatus.OK.code())
            .end(response);
    }

    @Override
    protected void handleGet(RoutingContext context) {
        log.debug(this.getClass().getName() + "handleGet invoqued ");
        SimpleAuthProvider.SimpleUser user = (SimpleAuthProvider.SimpleUser) context.user();
        boolean isAdmin = user.hasRole("admin");
        boolean transferMatchUser = true; // FIXME: TODO: implement
        if (!isAdmin && !transferMatchUser) {
            throw new InterledgerException(InterledgerException.RegisteredException.ForbiddenError);
        }
        LedgerTransferManager tm = SimpleLedgerTransferManager.getSingleton();
        TransferID transferID = new TransferID(context.request().getParam(transferUUID));
        LedgerTransfer transfer = tm.getTransferById(transferID);

        response(context, HttpResponseStatus.OK,
                buildJSON("result", ((SimpleLedgerTransfer) transfer).toWalletJSONStringifiedFormat(false /*bInclude Exec|Cancel Conditions*/)));
    }
}



