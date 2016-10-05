package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import org.interledger.ilp.common.api.handlers.EndpointHandler;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;

/**
 * Health handler
 *
 * @author mrmx
 */
public class TransferHandler extends RestEndpointHandler {

    public TransferHandler() {
        super("transfer");
    }

    public static EndpointHandler create() {
        return new TransferHandler();
    }
    
    @Override
    protected void handleGet(RoutingContext context) {
		// FIXME: TODO: Implement
		// GET /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 
		// GET /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment 
		// GET /transfers/9e97a403-f604-44de-9223-4ec36aa466d9/state 
		// GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7 
		// GET /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204/state?type=sha256 
    	throw new RuntimeException("Not implemented");
        // response(context,HttpResponseStatus.OK,buildJSONWith("status","OK"));
    }


    @Override
    protected void handlePut(RoutingContext context) {
    	// FIXME:TODO: Implement
		// PUT /transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204 
		// PUT /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment 
		// PUT /transfers/4e36fe38-8171-4aab-b60e-08d4b56fbbf1/rejection
    	
    	throw new RuntimeException("Not implemented");
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
 * *********************
 * * PUT/GET fulfillment (FROM ILP-CONNECTOR)
 * *********************
 * PUT /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment HTTP/1.1
 * cf:0:ZXhlY3V0ZQ
 * HTTP 1.1 200 OK
 * GET /transfers/25644640-d140-450e-b94b-badbe23d3389/fulfillment HTTP/1.1
 *     HTTP 1.1 200 OK
 *     cf:0:ZXhlY3V0ZQ
 * 
 * 
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
 *    {"id":"InsufficientFundsError","message":"Sender has insufficient funds.","owner":"alice"}
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
