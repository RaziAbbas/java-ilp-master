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
 */
public class TransferHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(TransferHandler.class);
    private final static String transferUUID  = "transferUUID",
                                execCondition = "execCondition";

	// GET|PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 
	// GET     /transfers/25644640-d140-450e-b94b-badbe23d3389/state|state?type=sha256 
	// GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7 

    // GET|PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 
    // GET|PUT /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment
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
        // FIXME: If debit's account owner != request credentials throw exception.
        // FIXME:TODO: Implement
        // PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 
        // PUT /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment 
        // PUT /transfers/4e36fe38-8171-4aab-b60e-08d4b56fbbf1/rejection
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

        // FIXME: TODO: Check that fromURI.getLedgerUri() match local ledger. Otherwise raise RuntimeException 
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
        // TODO: FIXME: ?context.request().response().end();?
    }

    @Override
    protected void handleGet(RoutingContext context) {
         // FIXME:TODO: Implement
        // GET /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment 
        // GET /transfers/4e36fe38-8171-4aab-b60e-08d4b56fbbf1/rejection
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
        boolean transferExists = tm.transferExists(transferID);
        if (!transferExists) {            
            throw notFound("Transfer not found");
        }
        LedgerTransfer transfer = tm.getTransferById(transferID);
        response(context, HttpResponseStatus.ACCEPTED,
                buildJSON("result", ((SimpleLedgerTransfer) transfer).toWalletJSONFormat()));
    }

}

/*
 * *************************
 * * PUT TRANSFER (from wallet):
 * *************************
 * PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 HTTP/1.1
 * Authorization: Basic YWxpY2U6YWxpY2U=
 * {"id":"http://localhost/transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204","ledger":"http://localhost","debits":[{"account":"http://localhost/accounts/alice","amount":"50"},{"account":"http://localhost/accounts/candice","amount":"20"}],"credits":[{"account":"http://localhost/accounts/bob","amount":"30"},{"account":"http://localhost/accounts/dave","amount":"40"}],"execution_condition":"cc:0:3:Xk14jPQJs7vrbEZ9FkeZPGBr0YqVzkpOYjFp_tIZMgs:7","expires_at":"2015-06-16T00:00:01.000Z","state":"prepared"}
 *     HTTP/1.1 201 Created
 *     {"id":"http://localhost/transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204","ledger":"http://localhost","debits":[{"account":"http://localhost/accounts/alice","amount":"50"},{"account":"http://localhost/accounts/candice","amount":"20"}],"credits":[{"account":"http://localhost/accounts/bob","amount":"30"},{"account":"http://localhost/accounts/dave","amount":"40"}],"execution_condition":"cc:0:3:Xk14jPQJs7vrbEZ9FkeZPGBr0YqVzkpOYjFp_tIZMgs:7","expires_at":"2015-06-16T00:00:01.000Z","state":"proposed","timeline":{"proposed_at":"2015-06-16T00:00:00.000Z"}}
 * 
 * *************************
 * * GET transfer by UUID
 * *************************
 * GET /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 HTTP/1.1
 *     HTTP/1.1 200 OK
 *     {"ledger":"http://localhost","execution_condition":"cc:0:3:Xk14jPQJs7vrbEZ9FkeZPGBr0YqVzkpOYjFp_tIZMgs:7","id":"http://localhost/transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204","expires_at":"2015-06-16T00:00:01.000Z","rejection_reason":"expired","state":"rejected","debits":[{"account":"http://localhost/accounts/alice","amount":"50"},{"account":"http://localhost/accounts/candice","amount":"20"}],"credits":[{"account":"http://localhost/accounts/bob","amount":"30"},{"account":"http://localhost/accounts/dave","amount":"40"}],"timeline":{"proposed_at":"2015-06-16T00:00:00.000Z","rejected_at":"2015-06-16T00:00:01.000Z"}}
 * 
 * *************************
 * * GET transfer state by UUID
 * *************************
 * GET /transfers/9e97a403-f604-44de-9223-4ec36aa466d9/state HTTP/1.1
 * HTTP/1.1 200 OK
 * {"type":"ed25519-sha512","message":{"id":"http://localhost/transfers/9e97a403-f604-44de-9223-4ec36aa466d9","state":"prepared"},"signer":"http://localhost","public_key":"YXg177AOkDlGGrBaoSET+UrMscbHGwFXHqfUMBZTtCY=","signature":"FSZPhUTrVOvpygekNth6eGgxqT3zUDOSDe7jm9KsfeNvsGlzuY1e81o64GPLBMABGU+TokcFgFH8yu4HURttBQ=="}
 * 
 * *************************
 * * GET transfer by Condition
 * *************************
 * GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7 HTTP/1.1
 *     HTTP/1.1 200 OK
 *     [{"ledger":"http://localhost","execution_condition":"cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7","cancellation_condition":"cc:0:3:I3TZF5S3n0-07JWH0s8ArsxPmVP6s-0d0SqxR6C3Ifk:6","id":"http://localhost/transfers/9e97a403-f604-44de-9223-4ec36aa466d9","state":"executed","debits":[{"account":"http://localhost/accounts/alice","amount":"10","authorized":true}],"credits":[{"account":"http://localhost/accounts/bob","amount":"10"}]}]
 * 
 * *************************
 * * GET transfer by UUID & type
 * *************************
 * GET /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204/state?type=sha256 HTTP/1.1
 * HTTP/1.1 200 OK
 * {"type":"sha256","message":{"id":"http://localhost/transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204","state":"proposed","token":"xy9kB4n/nWd+MsI84WeK2qg/tLfDr/4SIe5xO9OAz9PTmAwKOUzzJxY1+7c7e3rs0iQ0jy57L3U1Xu8852qlCg=="},"signer":"http://localhost","digest":"P6K2HEaZxAthBeGmbjeyPau0BIKjgkaPqW781zmSvf4="}
 * 
 * 
 * *************************
 * * GET transfer by executionCondition
 * *************************
 * GET /transfers/byExecutionCondition/cc:2:b:fc7hVoN43o57pcqzAHLadWuQ5ldiibFL4mJq_Vrn_68:29 HTTP/1.1
 *     HTTP/1.1 404 Not Found
 *     {"id":"NotFoundError","message":"Unknown execution condition"}
 * GET /transfers/byExecutionCondition/notanexecutioncondition HTTP/1.1
 *     HTTP/1.1 400 Bad Request
 *     {"id":"InvalidUriParameterError","message":"execution_condition is not a valid Condition","validationErrors":[{"message":"String does not match pattern: ^cc:([1-9a-f][0-9a-f]{0,3}|0):..."}]}
 * 
 * 
 * *************************
 * * POSSIBLE ERRORS:
 * *************************
 * PUT /transfers/155dff3f-4915-44df-a707-acc4b527bcbdbogus HTTP/1.1
 * {"ledger":"http://localhost","debits":[{"account":"http://localhost/accounts/alice","amount":"10","authorized":true}],"credits":[{"account":"http://localhost/accounts/bob","amount":"10"}],"state":"executed"}
 *    {"id":"ExpiredTransferError","message":"Cannot modify transfer after expires_at date"}
 *    {"id":"InsufficientFundsError","message":"Sender has insufficient fund)s.","owner":"alice"}
 *    {"id":"InvalidBodyError","message":"Body did not match schema Transfer","validationErrors":[{"message":"String does not match pattern: ^[-+]?[0-9]*[.]?[0-9]+([eE][-+]?[0-9]+)?$","params":{"pattern":"^[-+]?[0-9]*[.]?[0-9]+([eE][-+]?[0-9]+)?$"},"code":202,"dataPath":"/debits/0/amount","schemaPath":"/allOf/0/properties/debits/items/properties/amount/pattern","subErrors":null,"stack":" ...."}]}
 *    {"id":"InvalidModificationError","message":"Transfer may not be modified in this way","invalidDiffs":[{"kind":"A","path":["debits"],"index":1,"item":{"kind":"D","lhs":{"account":"candice","amount":"10"}}},{"kind":"E","path":["credits",0,"amount"],"lhs":"20","rhs":"10"}]}
 *    {"id":"InvalidModificationError","message":"Transfer may not be modified in this way","invalidDiffs":[{"kind":"D","path":["expires_at"],"lhs":"2015-06-16T00:00:01.000Z"}]}
 *    {"id":"InvalidModificationError","message":"Transfers in state executed may not be cancelled"}
 *    {"id":"InvalidModificationError","message":"Transfers in state executed may not be rejected"}
 *    {"id":"InvalidModificationError","message":"Transfers in state proposed may not be executed"}
 *    {"id":"InvalidUriParameterError","message":"id is not a valid Uuid","validationErrors":[{"message":"String does not match pattern: ^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$","params":{"pattern":"^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"},"code":202,"dataPath":"","schemaPath":"/pattern","subErrors":null,"stack":"..." }]}
 *    {"id":"NotFoundError","message":"Invalid transfer ID"}
 *    {"id":"NotFoundError","message":"Unknown transfer ID"}
 *    {"id":"TransferNotConditionalError","message":"Transfer is not conditional"}
 *    {"id":"UnauthorizedError","message":"Invalid attempt to authorize credit"}
 *    {"id":"UnauthorizedError","message":"Invalid attempt to authorize debit"}
 *    {"id":"UnauthorizedError","message":"Invalid attempt to reject credit"}
 *    {"id":"UnauthorizedError","message":"Invalid password"}
 *    {"id":"UnmetConditionError","message":"Fulfillment does not match any condition"}
 *    {"id":"UnprocessableEntityError","message":"Account `blob` does not exist."}
 *    {"id":"UnprocessableEntityError","message":"Amount exceeds allowed precision"}
 *    {"id":"UnprocessableEntityError","message":"Amount must be a positive number excluding zero."}
 *    {"id":"UnprocessableEntityError","message":"Total credits must equal total debits"}
 * 
 * 
 * *********************
 * * GET transfer ERRORS
 * *********************
 * GET /transfers/155dff3f-4915-44df-a707-acc4b527bcbd HTTP/1.1
 * Authorization: Basic YWRtaW46YWRtaW4=
 *     HTTP/1.1 404 Not Found
 *     {"id":"TransferNotFoundError"      ,"message":"This transfer does not exist"}
 *     {"id":"MissingFulfillmentError"    ,"message":"This transfer has not yet been fulfilled"}
 *     {"id":"MissingFulfillmentError"    ,"message":"This transfer expired before it was fulfilled"}
 *     {"id":"AlreadyRolledBackError"     ,"message":"This transfer has already been rejected"}
 *     {"id":"TransferNotConditionalError","message":"Transfer does not have any conditions"}
 *     {"id":"InvalidUriParameterError","message":"type is not valid"}
 *     {"id":"NotFoundError","message":"Unknown transfer ID"}
 * 
 * 
 * ******************************************
 * * PUT transfer-UUID rejection
 * ******************************************
 * PUT /transfers/4e36fe38-8171-4aab-b60e-08d4b56fbbf1/rejection HTTP/1.1
 * Authorization: Basic Ym9iOmJvYg==
 *     HTTP/1.1 201 Created
 * 
 * ******************************************
 * * PUT transfer-UUID rejection errors
 * ******************************************
 *     {"id":"InvalidModificationError","message":"Transfer may not be modified in this way","invalidDiffs":[{"kind":"E","path":["credits",0,"rejection_message"],"lhs":"ZXJyb3IgMQ==","rhs":"ZXJyb3IgMg=="}]}
 *     {"id":"UnauthorizedError","message":"Invalid attempt to reject credit"}
 * 
 */
///*
//// REF: https://github.com/interledger/five-bells-ledger/blob/master/src/sql/sqlite3/create.sql
//create table if not exists "L_LU_TRANSFER_STATUS" (
//  "STATUS_ID" integer not null primary key,
//  "NAME" varchar(20) not null,
//  "DESCRIPTION" varchar(255) null
//);
//
//
//create table if not exists "L_TRANSFERS" (
//  "TRANSFER_ID" integer not null primary key,
//  "TRANSFER_UUID" char(36) not null unique,
//  "LEDGER" varchar(1024),
//  "ADDITIONAL_INFO" text,
//  "STATUS_ID" integer not null,
//  "REJECTION_REASON_ID" integer,
//  "EXECUTION_CONDITION" text,
//  "CANCELLATION_CONDITION" text,
//  "EXPIRES_DTTM" datetime,
//  "PROPOSED_DTTM" datetime,
//  "PREPARED_DTTM" datetime,
//  "EXECUTED_DTTM" datetime,
//  "REJECTED_DTTM" datetime,
//  FOREIGN KEY("REJECTION_REASON_ID") REFERENCES "L_LU_REJECTION_REASON"
//    ("REJECTION_REASON_ID"),
//  FOREIGN KEY("STATUS_ID") REFERENCES "L_LU_TRANSFER_STATUS" ("STATUS_ID")
//);
//
//create table if not exists "L_TRANSFER_ADJUSTMENTS"
//(
//  "TRANSFER_ADJUSTMENT_ID" integer not null primary key,
//  "TRANSFER_ID" integer not null,
//  "ACCOUNT_ID" integer not null,
//  "DEBIT_CREDIT" varchar(10) not null,
//  "AMOUNT" float DEFAULT 0 not null,
//  "IS_AUTHORIZED" boolean default 0 not null,
//  "IS_REJECTED" boolean default 0 not null,
//  "REJECTION_MESSAGE" text,
//  "MEMO" varchar(4000) null,
//  FOREIGN KEY("TRANSFER_ID") REFERENCES "L_TRANSFERS" ("TRANSFER_ID"),
//  FOREIGN KEY("ACCOUNT_ID") REFERENCES "L_ACCOUNTS" ("ACCOUNT_ID")
//);
//
//create table if not exists "L_FULFILLMENTS" (
//"FULFILLMENT_ID" integer not null primary key,
//"TRANSFER_ID" integer,
//"CONDITION_FULFILLMENT" text
//);
