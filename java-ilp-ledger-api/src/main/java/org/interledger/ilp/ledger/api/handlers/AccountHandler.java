package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import static io.vertx.core.http.HttpMethod.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.interledger.ilp.common.api.auth.AuthInfo;
import org.interledger.ilp.common.api.auth.AuthManager;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
import org.interledger.ilp.common.api.util.JsonObjectBuilder;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.account.AccountNotFoundException;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single Account handler
 *
 * @author mrmx
 */
public class AccountHandler extends RestEndpointHandler {

    private static final Logger log = LoggerFactory.getLogger(AccountHandler.class);

    private final static String PARAM_NAME = "name";
    private final static String PARAM_BALANCE = "balance";

    public AccountHandler() {
        super("account", "accounts/:" + PARAM_NAME);
        accept(GET, POST);
    }

    public static AccountHandler create() {
        return new AccountHandler();
    }

    protected void handleGet(RoutingContext context) {
        //String accountName = getAccountName(context);
        AuthInfo authInfo = AuthManager.getInstance().getAuthInfo(context);
        if (authInfo.isEmpty()) {
            String accountName = getAccountName(context);
            if (StringUtils.isNotBlank(accountName)) {
                handleAuthorized(context, null);
            } else {
                bad(context, "Required param " + PARAM_NAME);
            }
        } else {
            AuthManager.getInstance().getAuthProvider().authenticate(authInfo.getPrincipal(), res -> {
                if (res.succeeded()) {
                    handleAuthorized(context, res.result());
                } else {
                    handleUnAuthorized(context, authInfo);
                }
            });
        }
    }

    protected void handlePost(RoutingContext context) {
        log.debug("Handing put account");
        LedgerInfo ledgerInfo = LedgerFactory.getDefaultLedger().getInfo();
        String accountName = getAccountName(context);
        JsonObject requestBody = getBodyAsJson(context);
        Double balance = requestBody.getDouble(PARAM_BALANCE, 100d);
        ledgerInfo.getCurrencyCode();

        log.debug("Put account {} balance: {}{}", accountName, balance, ledgerInfo.getCurrencyCode());
        LedgerAccount account = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton().create(accountName);
        account.setBalance(balance);
        response(context, HttpResponseStatus.CREATED,
                buildJSON("result", Json.encode(account)));
    }

    private LedgerAccount getAccountByName(RoutingContext context) {
        String accountName = getAccountNameOrThrowException(context);
        log.debug("Get account {}", accountName);
        try {
            return LedgerAccountManagerFactory.getLedgerAccountManagerSingleton().getAccountByName(accountName);
        } catch (AccountNotFoundException ex) {
            throw notFound("Unknown account");
        }

    }

    private String getAccountName(RoutingContext context) {
        return context.request().getParam(PARAM_NAME);
    }

    private String getAccountNameOrThrowException(RoutingContext context) {
        String accountName = context.request().getParam(PARAM_NAME);
        if (StringUtils.isBlank(accountName)) {
            throw new RestEndpointException(HttpResponseStatus.BAD_REQUEST, accountName);
        }
        return accountName;
    }

    private void handleAuthorized(RoutingContext context, User user) {
        log.info("handleAuthorized {}", user);
        //boolean isAdmin = ((UserRole)user).hasRole("admin");        
        LedgerAccount account = getAccountByName(context);
        JsonObject result = JsonObjectBuilder.create()
                .put("id", account.getUri())
                .put("name", account.getName())
                .put("ledger", LedgerFactory.getDefaultLedger().getInfo().getBaseUri())
                .get();
        response(context, HttpResponseStatus.OK, result);
    }

    private void handleUnAuthorized(RoutingContext context, AuthInfo authInfo) {
        log.info("handleUnAuthorized {}", authInfo);
        forbidden(context);
    }
}
