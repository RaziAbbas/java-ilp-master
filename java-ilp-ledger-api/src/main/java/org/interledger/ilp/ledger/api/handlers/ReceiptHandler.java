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
public class ReceiptHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(ReceiptHandler.class);
    private final static String transferUUID  = "transferUUID";

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
        /*
         *  TODO: FIXME: router.get('/transfers/:id/state', transfers.getStateResource)
         *  
         *  function * getStateResource () {
         *      const conditionState = this.query.condition_state
         *      const receiptType = this.query.type || 'ed25519-sha512'
         *      const receiptTypes = ['ed25519-sha512', 'sha256']
         *      if (!_.includes(receiptTypes, receiptType)) {
         *        throw new InvalidUriParameterError('type is not valid')
         *      }
         *      this.body = yield model.getTransferStateReceipt(
         *        id.toLowerCase(), receiptType, conditionState)
         * } 
         * REF: five-bells-ledger/src/models/transfers.js
         * 
         * function * getTransferStateReceipt (id, receiptType, conditionState) {
         *   const transfer = yield db.getTransfer(id)
         *   const transferState = transfer ? transfer.state : transferStates.TRANSFER_STATE_NONEXISTENT
         *   if (receiptType === RECEIPT_TYPE_ED25519) {
         *     return makeEd25519Receipt(uri.make('transfer', id), transferState)
         *   } else if (receiptType === RECEIPT_TYPE_SHA256) {
         *     return makeSha256Receipt(uri.make('transfer', id), transferState, conditionState)
         *   } else {
         *     throw new UnprocessableEntityError('type is not valid')
         *   }
         * }
         * function makeEd25519Receipt (transferId, transferState) {
         *   const message = makeTransferStateMessage(transferId, transferState, RECEIPT_TYPE_ED25519)
         *   return {
         *     type: RECEIPT_TYPE_ED25519,
         *     message: message,
         *     signer: config.getIn(['server', 'base_uri']),
         *     public_key: config.getIn(['keys', 'ed25519', 'public']),
         *     signature: sign(hashJSON(message))
         *   }
         * }
         * function makeSha256Receipt (transferId, transferState, conditionState) {
         *   const message = makeTransferStateMessage(transferId, transferState, RECEIPT_TYPE_SHA256)
         *   const receipt = {
         *     type: RECEIPT_TYPE_SHA256,
         *     message: message,
         *     signer: config.getIn(['server', 'base_uri']),
         *     digest: sha256(stringifyJSON(message))
         *   }
         *   if (conditionState) {
         *     const conditionMessage = makeTransferStateMessage(transferId, conditionState, RECEIPT_TYPE_SHA256)
         *     receipt.condition_state = conditionState
         *     receipt.condition_digest = sha256(stringifyJSON(conditionMessage))
         *   }
         *   return receipt
         * }
         */

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
