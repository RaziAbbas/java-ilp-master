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
        RoleUser user = (RoleUser) context.user();
        boolean isAdmin = user.hasRole("admin");
        boolean transferMatchUser = true; // FIXME: TODO: implement
        if (!isAdmin && !transferMatchUser) {
            throw new InterledgerException(InterledgerException.RegisteredException.ForbiddenError);
        }
        log.debug(context.getBodyAsString());

        /*
        * Received json message will be similar to: TODO: Recheck
        * {
        *   "ledger": "http://localhost:3000/",
        *   "from"  : "http://localhost:3000/accounts/alice",
        *   "to"    : "http://localhost:3000/accounts/http%3A%2F%2Flocalhost%3A4000",
        *   "data"  : {"method":"quote_request",
        *               "data":{
        *                 "source_address":"ledger1.eur.alice",
        *                 "destination_address":"ledger2.eur.alice.0a046f8e-b3d5-433d-a813-1cf01461a97c",
        *                 "destination_amount":"10"
        *               },
        *               "id"    : "67599ddd-f3dc-403a-bdce-3e3b98c1f82e"
        *             }
        * }
         */
        // FIXME: Recheck/Implement
        JsonObject jsonMessageReceived = getBodyAsJson(context);
        String userName = user.getAuthInfo().getUsername();
        /*
         * function * sendMessage (message, requestingUser) {
         * 
         *   const recipientName = uri.parse(message.account, 'account').name.toLowerCase()
         *   const senderAccount = uri.make('account', requestingUser.name)
         *   yield notificationBroadcaster.sendMessage(recipientName, {
         *     ledger: message.ledger,
         *     account: senderAccount,
         *     data: message.data
         *   })
         * }
         */
        String notification = "TODO(message, userName)";
        TransferWSEventHandler.notifyILPConnector(context, notification);
        String response = "OK";
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .putHeader(HttpHeaders.CONTENT_LENGTH, "" + response.length())
                .setStatusCode(HttpResponseStatus.CREATED.code())
                .end(response);
    }

}
