package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;

//import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.PUT;

import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.auth.impl.SimpleAuthProvider;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerTransfer;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerTransferManager;
import org.interledger.ilp.ledger.transfer.LedgerTransferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fulfillment (and rejection) handler
 *
 * @author earizon
 */
public class FulfillmentHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(FulfillmentHandler.class);
    private final static String transferUUID= "transferUUID";

	// GET|PUT /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment

	// PUT /transfers/4e36fe38-8171-4aab-b60e-08d4b56fbbf1/rejection
	// GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7 

    public FulfillmentHandler() {
        super("transfer", new String[] 
            {
                "transfers/:" + transferUUID + "/fulfillment",
                "transfers/:" + transferUUID + "/rejection",
            });
        accept(GET, PUT);
    }

//    public TransferHandler with(LedgerAccountManager ledgerAccountManager) {
//        this.ledgerAccountManager = ledgerAccountManager;
//        return this;
//    }

    public static FulfillmentHandler create() {
        return new FulfillmentHandler(); // TODO: return singleton?
    }

    @Override
    protected void handlePut(RoutingContext context) {
        // FIXME: If debit's account owner != request credentials throw exception.
        // FIXME:TODO: Implement
        // PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 
        // PUT /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment 
        // PUT /transfers/4e36fe38-8171-4aab-b60e-08d4b56fbbf1/rejection
        log.debug(this.getClass().getName() + "handlePut invoqued ");
        /**********************
         * PUT/GET fulfillment (FROM ILP-CONNECTOR)
         *********************
         *
         * PUT /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment HTTP/1.1
         *     HTTP 1.1 200 OK
         *     cf:0:ZXhlY3V0ZQ
         * 
         * GET /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment HTTP/1.1
         *     HTTP 1.1 200 OK
         *     cf:0:ZXhlY3V0ZQ
         */
        TransferID transferID = new TransferID(context.request().getParam(transferUUID));
        LedgerTransferManager tm = SimpleLedgerTransferManager.getSingleton();
        boolean transferExists = tm.transferExists(transferID);
        if (!transferExists) { 
            // FIXME: Return correct HTTP code 40x. 
            // throwing a RuntimeException returns "ERROR 500: Internal Server Error"
            throw new RuntimeException("Transfer not found");
        }
        LedgerTransfer transfer = tm.getTransferById(transferID);
        
        /*
         * REF: five-bells-ledger/src/lib/app.js
         *     _makeRouter () {
         *         router.put('/transfers/:id/fulfillment', transfers.putFulfillment)
         *
         * REF: five-bells-ledger/src/controllers/transfers.js
         *     const model = require('../models/transfers')
         *     ...
         *     function * putFulfillment () {
         *         const id = this.params.id
         *         const fulfillment = yield parse.text(this)
         *         const result = yield model.fulfillTransfer(id, fulfillment)
         *         this.body = result.fulfillment
         *         this.status = result.existed ? 200 : 201
         * // REF: five-bells-ledger/src/models/transfers.js
         * function * fulfillTransfer (transferId, fulfillmentUri) {
         *     const fulfillment = convertToInternalFulfillment(fulfillmentUri)
         *     const existingFulfillment = yield db.getTransfer(transferId) // || or throw exception
         *     const conditionType = validateConditionFulfillment(transfer, fulfillment)
         *     if (  (conditionType === CONDITION_TYPE_EXECUTION    && transfer.state === transferStates.TRANSFER_STATE_EXECUTED)
         *        || (conditionType === CONDITION_TYPE_CANCELLATION && transfer.state === transferStates.TRANSFER_STATE_REJECTED)
         *     ) { return convertToExternalFulfillment(yield fulfillments.getFulfillment(transferId, {transaction})) }
         *     if (conditionType === CONDITION_TYPE_EXECUTION) {
         *       if (!_.includes(validExecutionStates, transfer.state)) {
         *         throw new InvalidModificationError('Transfers in state ' + transfer.state + ' may not be executed')
         *       }
         *       yield executeTransfer(transaction, transfer, fulfillment)
         *     } else if (conditionType === CONDITION_TYPE_CANCELLATION) {
         *       if (!_.includes(validCancellationStates, transfer.state)) {
         *         throw new InvalidModificationError('Transfers in state ' + transfer.state + ' may not be cancelled') }
         *       yield cancelTransfer(transaction, transfer, fulfillment)
         *     }
         *     transferExpiryMonitor.unwatch(transfer.id)
         *     db.updateTransfer(transfer, {transaction})
         *     notificationBroadcaster.sendNotifications(transfer, transaction)
         *     // Start the expiry countdown if the transfer is not yet finalized
         *     // If the expires_at has passed by this time we'll consider
         *     // the transfer to have made it in before the deadline
         *     if (!isTransferFinalized(transfer)) {
         *       yield transferExpiryMonitor.watch(transfer)
         *     }
         * 
         *     return {
         *       fulfillment: existingFulfillment || convertToExternalFulfillment(fulfillment),
         *       existed: Boolean(existingFulfillment)
         *     }
         * }
         *
         * REF: five-bells-ledger/src/controllers/transfers.js
         * 
         * function validateConditionFulfillment (transfer, fulfillmentModel) {
         *     if (!transfer.execution_condition && !transfer.cancellation_condition) {
         *       throw new TransferNotConditionalError('Transfer is not conditional')
         *     }
         *     const fulfillment = convertToExternalFulfillment(fulfillmentModel)
         *                                             try {
         *     const condition = cc.fulfillmentToCondition(fulfillment)
         *     if ( condition === transfer.execution_condition &&
         *          cc.validateFulfillment(fulfillment, condition) ) {
         *       return CONDITION_TYPE_EXECUTION
         *     } else if ( condition === transfer.cancellation_condition &&
         *                 cc.validateFulfillment(fulfillment, condition) ) {
         *       return CONDITION_TYPE_CANCELLATION
         *     }
         *                                             } catch (err) {
         *     throw new InvalidBodyError('Invalid fulfillment: ' + err.toString())
         *                                             }
         *     throw new UnmetConditionError('Fulfillment does not match any condition')
         * }
         */
        // TODO: FIXME: Implement PUT Fulfillment/Rejection
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
            unauthorized(context);
            return;
        }
        TransferID transferID = new TransferID(context.request().getParam(transferUUID));
        LedgerTransferManager tm = SimpleLedgerTransferManager.getSingleton();
        boolean transferExists = tm.transferExists(transferID);
        if (!transferExists) { 
            // FIXME: Return correct HTTP code 40x. 
            // throwing a RuntimeException returns "ERROR 500: Internal Server Error"
            throw new RuntimeException("Transfer not found");
        }
        LedgerTransfer transfer = tm.getTransferById(transferID);
        response(context, HttpResponseStatus.ACCEPTED,
                buildJSON("result", ((SimpleLedgerTransfer)transfer).toWalletJSONFormat()));
    }
}

