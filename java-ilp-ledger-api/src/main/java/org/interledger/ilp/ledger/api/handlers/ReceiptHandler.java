package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import static io.vertx.core.http.HttpMethod.GET;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.auth.impl.SimpleAuthProvider;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;

import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.core.TransferStatus;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerTransferManager;
import org.interledger.ilp.ledger.transfer.LedgerTransferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransferHandler handler
 *
 * @author earizon
 */
public class ReceiptHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(ReceiptHandler.class);
    private final static String transferUUID  = "transferUUID";
    private static final String RECEIPT_TYPE_ED25519 = "ed25519-sha512",
                                RECEIPT_TYPE_SHA256  = "sha256";

    // GET     /transfers/25644640-d140-450e-b94b-badbe23d3389/state|state?type=sha256 
    // PUT /transfers/4e36fe38-8171-4aab-b60e-08d4b56fbbf1/rejection
    // GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7 
    public ReceiptHandler() {
        // REF: https://github.com/interledger/five-bells-ledger/blob/master/src/lib/app.js
        super("transfer", new String[] 
            {
                "transfers/:" + transferUUID + "/state", 
            });
        accept(GET);
    }

    public static ReceiptHandler create() {
        return new ReceiptHandler(); // TODO: return singleton?
    }
    
    private static JsonObject makeTransferStateMessage(String transferId, TransferStatus state, String receiptType) {
        JsonObject jo = new JsonObject();
        jo.put("id", transferId);
        jo.put("state", state);
        if (receiptType.equals(RECEIPT_TYPE_SHA256)) {
            String token = ""; // FIXME: sign(sha512(transferId + ':' + state))
            jo.put("token", token);
        }
        return jo;
    }

    @Override
    protected void handleGet(RoutingContext context) {
        /*
         * *************************
         * * GET transfer by UUID & type
         * *************************
         * GET /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204/state?type=sha256 HTTP/1.1
         * HTTP/1.1 200 OK
         * {"type":"sha256","message":{"id":"http://localhost/transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204","state":"proposed","token":"xy9kB4n/nWd+MsI84WeK2qg/tLfDr/4SIe5xO9OAz9PTmAwKOUzzJxY1+7c7e3rs0iQ0jy57L3U1Xu8852qlCg=="},"signer":"http://localhost","digest":"P6K2HEaZxAthBeGmbjeyPau0BIKjgkaPqW781zmSvf4="}
         */
        log.debug(this.getClass().getName() + "handleGet invoqued ");
        SimpleAuthProvider.SimpleUser user = (SimpleAuthProvider.SimpleUser) context.user();
        boolean isAdmin = user.hasRole("admin");
        boolean transferMatchUser = true; // FIXME: TODO: implement
        if (!isAdmin && !transferMatchUser) {
            forbidden(context);
            return;
        }
        String transferId = context.request().getParam(transferUUID);
        TransferID transferID = new TransferID(transferId);
        LedgerTransferManager tm = SimpleLedgerTransferManager.getSingleton();
        TransferStatus status = TransferStatus.NONEXISTENT;
        if (tm.transferExists(transferID)) { 
            LedgerTransfer transfer = tm.getTransferById(transferID);
            status = transfer.getTransferStatus();
        }
        
        /* FIXME:
         *      this.body = yield model.getTransferStateReceipt(
         *        id.toLowerCase(), receiptType, conditionState)
         */
        // REF: getStateResource @ transfers.js
        
        

        String receiptType = context.request().getParam("type");
        // REF: getTransferStateReceipt(id, receiptType, conditionState) @ five-bells-ledger/src/models/transfers.js
        if (receiptType == null) { receiptType = RECEIPT_TYPE_ED25519; }
        if ( ! receiptType.equals(RECEIPT_TYPE_ED25519) &&
             ! receiptType.equals(RECEIPT_TYPE_SHA256 ) &&
             true ) {
            throw new RuntimeException("type not in := "+RECEIPT_TYPE_ED25519+"* | "+RECEIPT_TYPE_SHA256+" ");
        }
        JsonObject jo = new JsonObject();
        String signer = "";      // FIXME: config.getIn(['server', 'base_uri']),
        if (receiptType.equals(RECEIPT_TYPE_ED25519)) {
            // REF: makeEd25519Receipt(transferId, transferState) @
            //      @ five-bells-ledger/src/models/transfers.js
            JsonObject message = makeTransferStateMessage(transferId, status, RECEIPT_TYPE_ED25519);
            String public_key = "";  // FIXME: config.getIn(['keys', 'ed25519', 'public']),
            String signature = "";   // FIXME: sign(hashJSON(message))
            jo.put("type", RECEIPT_TYPE_ED25519);
            jo.put("message", message);
            jo.put("signer", signer);
            jo.put("public_key", public_key);
            jo.put(signature, signature);
        } else {
            // REF: makeSha256Receipt(transferId, transferState, conditionState) @
            //      @ five-bells-ledger/src/models/transfers.js
            JsonObject message = makeTransferStateMessage(transferId, status, RECEIPT_TYPE_SHA256);
            String digest = "";     // FIXME: sha256(stringifyJSON(message))
            jo.put("type", RECEIPT_TYPE_SHA256);
            jo.put("message", message);
            jo.put("signer", signer);
            jo.put("digest", digest);
            String conditionState = context.request().getParam("condition_state");
            if (conditionState != null) {
                JsonObject conditionMessage = makeTransferStateMessage(transferId, status, RECEIPT_TYPE_SHA256);
                String condition_digest = "TODO sha256 of "+conditionMessage; // FIXME sha256(conditionMessage.encode())
                jo.put("condition_state", conditionState);
                jo.put("condition_digest", condition_digest);
            }
        }

        String response = jo.encode();
        context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
        .putHeader(HttpHeaders.CONTENT_LENGTH, ""+response.length())
        .setStatusCode(HttpResponseStatus.ACCEPTED.code())
        .end(response);
    }

}
