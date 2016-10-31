package org.interledger.ilp.ledger.api.handlers;

import java.util.List;

import io.netty.handler.codec.http.HttpResponseStatus;
import static io.vertx.core.http.HttpMethod.GET;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.auth.impl.SimpleAuthProvider;
import org.interledger.ilp.common.api.core.InterledgerException;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
import org.interledger.ilp.core.ConditionURI;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerTransfer;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerTransferManager;
import org.interledger.ilp.ledger.transfer.LedgerTransferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransferHandler handler
 *
 * @author earizon
 * REF: five-bells-ledger/src/controllers/transfers.js
 */
public class TransfersHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(TransfersHandler.class);
    private final static String execCondition = "execCondition";
    // GET /transfers/byExecutionCondition/cc:0:3:vmvf6B7EpFalN6RGDx9F4f4z0wtOIgsIdCmbgv06ceI:7 

    public TransfersHandler() {
        // REF: https://github.com/interledger/five-bells-ledger/blob/master/src/lib/app.js
        super("transfer", new String[] 
            {
                "transfers/byExecutionCondition/:" + execCondition
            });
        accept(GET);
    }

//    public TransferHandler with(LedgerAccountManager ledgerAccountManager) {
//        this.ledgerAccountManager = ledgerAccountManager;
//        return this;
//    }
    public static TransfersHandler create() {
        return new TransfersHandler(); // TODO: return singleton?
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
        log.info(this.getClass().getName() + "handleGet invoqued ");
        SimpleAuthProvider.SimpleUser user = (SimpleAuthProvider.SimpleUser) context.user();
        boolean isAdmin = user.hasRole("admin");
        boolean transferMatchUser = true; // FIXME: TODO: implement
        if (!isAdmin && !transferMatchUser) {
            throw new InterledgerException(InterledgerException.RegisteredException.ForbiddenError);
        }
        LedgerTransferManager tm = SimpleLedgerTransferManager.getSingleton();
        ConditionURI executionCondition = ConditionURI.build(context.request().getParam(execCondition));
        List<LedgerTransfer> transferList = tm.getTransfersByExecutionCondition(executionCondition);
        JsonArray ja = new JsonArray();
        for (LedgerTransfer transfer : transferList) {
            ja.add(((SimpleLedgerTransfer)transfer).toJSONWalletFormat(true /*include exec|cancel conditions in response*/));
        }
        String response = ja.encode();
        context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .putHeader(HttpHeaders.CONTENT_LENGTH, ""+response.length())
            .setStatusCode(HttpResponseStatus.OK.code())
            .end(response);
    }

    /*
By executionCondition:
function * getTransfersByExecutionCondition (executionCondition) {
  const transfers = yield db.getTransfersByExecutionCondition(executionCondition)
  if (_.isEmpty(transfers)) {
    throw new NotFoundError('Unknown execution condition')
  }

  return Promise.all(transfers.map(converters.convertToExternalTransfer))
}
     */
}



