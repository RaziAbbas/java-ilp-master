package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;
import static io.vertx.core.http.HttpMethod.PUT;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.auth.impl.SimpleAuthProvider;
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
	// GET     /transfers/25644640-d140-450e-b94b-badbe23d3389/state|state?type=sha256 
	// GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7 

    // GET|PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 
    // GET     /transfers/25644640-d140-450e-b94b-badbe23d3389/state|state?type=sha256 
    // PUT /transfers/4e36fe38-8171-4aab-b60e-08d4b56fbbf1/rejection
    // GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7 
    public TransferHandler() {
        // REF: https://github.com/interledger/five-bells-ledger/blob/master/src/lib/app.js
        super("transfer", new String[] 
            {
                "transfers/:" + transferUUID,
                "transfers/:" + transferUUID + "/state",
                "transfers/byExecutionCondition/:" + execCondition

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
            forbidden(context);
            return;
        }
        log.debug(this.getClass().getName() + "handlePut invoqued ");
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
        TransferID transferID = new TransferID(context.request().getParam(transferUUID));

        JsonObject requestBody = getBodyAsJson(context);
        String state = requestBody.getString("state");
        if (state == null) {
            state = "proposed";
        }
        if (!"proposed".equals(state)) {
            throw new RuntimeException("state must be 'proposed' for new transactions");
        }
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
        boolean isLocalTransaction = fromURI0.getLedgerUri().equals(toURI0.getLedgerUri());
        log.debug(">>> is local lransaction?: " + isLocalTransaction);
        if (isLocalTransaction) {
            if (!debit0_ammount.equals(credit0_ammount)) {
                throw new RuntimeException("WARN: SECURITY EXCEPTION: "
                        + "debit '" + debit0_ammount.toString() + "' is different to "
                        + "credit '" + credit0_ammount.toString() + "'");
            }
            tm.executeLocalTransfer(fromURI0, toURI0, debit0_ammount);
            response(context, HttpResponseStatus.CREATED, requestBody);
        } else {
            ConditionURI URIExecutionCond = new ConditionURI(requestBody.getString("execution_condition"));
            String cancelation_condition = requestBody.getString("cancelation_condition");

            ConditionURI URICancelationCond = (cancelation_condition != null)
                    ? new ConditionURI(cancelation_condition) : ConditionURI.EMPTY;
            DTTM DTTM_expires = requestBody.getString("expires_at") != null
                    ? new DTTM(requestBody.getString("expires_at"))
                    : DTTM.future; // TODO: RECHECK
            DTTM DTTM_proposed = DTTM.getNow();
            String data = ""; // Not used
            String noteToSelf = ""; // Not used
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
                String notification = ((SimpleLedgerTransfer) effectiveTransfer).toILPJSONFormat();
                log.debug("send transfer update to ILP Connector through websocket: \n:" + notification + "\n");
                TransferWSEventHandler.notifyILPConnector(context,
                        notification);
            } catch (Exception e) {
                log.warn("transaction created correctly but ilp-connector couldn't be notified due to " + e.toString());
                /* FIXME:(improvement) The message must be added to a pool of pending event notifications to 
                 *     send to the connector once the (websocket) connection is restablished.
                 */
            }
            response(context,
                    isNewTransfer ? HttpResponseStatus.CREATED : HttpResponseStatus.ACCEPTED,
                    buildJSON("result", ((SimpleLedgerTransfer) effectiveTransfer).toWalletJSONFormat()));
        }
    }

    @Override
    protected void handleGet(RoutingContext context) {
        /* 
         * FIXME: TODO
         *  *************************
         *  * GET transfer by Condition
         *  *************************
         *  GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN...I:7 HTTP/1.1
         *      HTTP/1.1 200 OK
         *      [{"ledger":"http://localhost",
         *        "execution_condition":"cc:0:3:vmvf6B7EpFalN...I:7",
         *        "cancellation_condition":"cc:0:3:I3TZF5S3n0-...:6",
         *        "id":"http://localhost/transfers/9e97a403-f604-44de-9223-4ec36aa466d9",
         *        "state":"executed",
         *        "debits":[
         *          {"account":"http://localhost/accounts/alice","amount":"10","authorized":true}],
         *        "credits":[{"account":"http://localhost/accounts/bob","amount":"10"}]}]
         */
        log.debug(this.getClass().getName() + "handleGet invoqued ");
        SimpleAuthProvider.SimpleUser user = (SimpleAuthProvider.SimpleUser) context.user();
        boolean isAdmin = user.hasRole("admin");
        boolean transferMatchUser = true; // FIXME: TODO: implement
        if (!isAdmin && !transferMatchUser) {
            forbidden(context);
            return;
        }
        TransferID transferID = new TransferID(context.request().getParam(transferUUID));
        LedgerTransferManager tm = SimpleLedgerTransferManager.getSingleton();

        LedgerTransfer transfer = tm.getTransferById(transferID);
        response(context, HttpResponseStatus.ACCEPTED,
                buildJSON("result", ((SimpleLedgerTransfer) transfer).toWalletJSONFormat()));
    }

}



