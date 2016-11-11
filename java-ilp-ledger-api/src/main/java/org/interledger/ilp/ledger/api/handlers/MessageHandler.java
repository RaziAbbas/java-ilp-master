package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import static io.vertx.core.http.HttpMethod.POST;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.auth.RoleUser;
import org.interledger.ilp.common.api.core.InterledgerException;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
import org.interledger.ilp.core.InterledgerPacketHeader;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerTransferManager;
import org.interledger.ilp.ledger.transfer.LedgerTransferManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransferHandler handler
 *
 * REF: five-bells-ledger/src/controllers/transfers.js
 * 
 * @author earizon 
 */
public class MessageHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    public MessageHandler() {
        super("messages", "messages");
        accept(POST);
    }

    public static MessageHandler create() {
        return new MessageHandler(); // TODO: return singleton?
    }

    @Override
    protected void handlePost(RoutingContext context) {
        /*
         *  TODO: FIXME: Create validation infrastructure for Messages, similar to the JS code:
         *  const validationResult = validator.create('Message')(message)$
         *  if (validationResult.valid !== true) { $... }
         */
        SimpleAuthProvider.SimpleUser user = (SimpleAuthProvider.SimpleUser) context.user();
        boolean isAdmin = user.hasRole("admin");
        boolean transferMatchUser = true; // FIXME: TODO: implement
        if (!isAdmin && !transferMatchUser) {
            throw new InterledgerException(InterledgerException.RegisteredException.ForbiddenError);
        }
        log.info("handlePost context.getBodyAsString():\n   "+context.getBodyAsString());

        /*
        * Received json message will be similar to: TODO: Recheck
        * postMessage (data.method == quote_request) is similar to:
        * {
        *   "ledger": "http://localhost:3000/",
        *   "from"  : "http://localhost:3000/accounts/alice",
        *   "to"    : "http://localhost:3000/accounts/ilpconnector",
        *   "data"  : {"method":"quote_request",
        *               "data":{
        *                 "source_address":"ledger1.eur.alice",
        *                 "destination_address":"ledger2.eur.alice.0a046f8e-b3d5-433d-a813-1cf01461a97c",
        *                 "destination_amount":"10"
        *               },
        *               "id"    : "67599ddd-f3dc-403a-bdce-3e3b98c1f82e"
        *             }
        * }
        * 
        * or (data.method == quote_response)
        * 
        * {
        *   "ledger":"http://localhost:3002",
        *   "account":"http://localhost:3002/accounts/alice",
        *   "data":{
        *       "id":"434b23c4-9b53-4e4e-8dc2-8d3ca6479736",
        *       "method":"quote_response",
        *       "data":
        *           {
        *               "source_ledger":"ledger2.eur.",
        *               "destination_ledger":"ledger3.eur.",
        *               "source_connector_account":"ledger2.eur.ilpconnector",
        *               "source_amount":"1.01",
        *               "destination_amount":"1.00",
        *               "source_expiry_duration":"6",
        *               "destination_expiry_duration":"5"}
        *           }
        *       }
        * }
        */
        // FIXME: Recheck
        JsonObject jsonMessageReceived = getBodyAsJson(context);

        // For backwards compatibility. (Ref: messages.js @ five-bells-ledger)
        if (jsonMessageReceived.getString("account") !=null && 
            jsonMessageReceived.getString("from") == null && 
            jsonMessageReceived.getString("to") == null) {
            jsonMessageReceived.put("to",jsonMessageReceived.getString("account"));
            LedgerInfo ledgerInfo = LedgerFactory.getDefaultLedger().getInfo();
            jsonMessageReceived.put("from", ledgerInfo.getBaseUri() + "accounts/" + user.getUsername());
        }
        JsonObject data = jsonMessageReceived.getJsonObject("data");
        JsonObject data_data = data.getJsonObject("data");

        String recipient = jsonMessageReceived.getString("to");
        log.debug("deleteme: recipient:"+recipient+" , recipient.lastIndexOf('/')"+recipient.lastIndexOf('/'));
               recipient = recipient.substring(recipient.lastIndexOf('/')+1);
        String from = jsonMessageReceived.getString("from");
               from = from.substring(from.lastIndexOf('/')+1);

        LedgerAccount account = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton().getAccountByName(from);

        String URIAccount = account.getUri();
        /*
         * REF: sendMessage @ src/model/messajes.js : Add account to message:
         * yield notificationBroadcaster.sendMessage(recipientName,
         *    Object.assign({}, message, {account: senderAccount}))
         */
        jsonMessageReceived.put("account",  URIAccount);
        
        // REF: sendMessage @ models/messages.js:
        JsonObject notificationJSON = new JsonObject();
        notificationJSON.put("type", "message");
        notificationJSON.put("resource", jsonMessageReceived);
        String notification = notificationJSON.encode();
        TransferWSEventHandler.notifyListener(context, recipient, notification);
        String response = context.getBodyAsString();
        context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .putHeader(HttpHeaders.CONTENT_LENGTH, ""+response.length())
            .setStatusCode(HttpResponseStatus.CREATED.code() /*201*/)
            .end(response);
    }

}
