package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import static io.vertx.core.http.HttpMethod.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.interledger.ilp.common.api.auth.AuthInfo;
import org.interledger.ilp.common.api.auth.AuthManager;
import org.interledger.ilp.common.api.auth.RoleUser;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
import org.interledger.ilp.common.api.util.JsonObjectBuilder;
import org.interledger.ilp.common.api.util.NumberConversionUtils;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.account.AccountNotFoundException;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.interledger.ilp.ledger.account.LedgerAccountManager;
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
    private final static String PARAM_MIN_ALLOWED_BALANCE = "minimum_allowed_balance";

    public AccountHandler() {
        super("account", "accounts/:" + PARAM_NAME);
        accept(GET, PUT);
    }

    public static AccountHandler create() {
        return new AccountHandler();
    }

    @Override
    protected void handleGet(RoutingContext context) {
        AuthManager.getInstance().authenticate(context, res -> {
            AuthInfo authInfo = res.result();
            if (res.succeeded()) {
                handleAuthorizedGet(context, authInfo);
            } else if (authInfo.isEmpty()) {
                String accountName = getAccountName(context);
                if (StringUtils.isNotBlank(accountName)) {
                    handleAuthorizedGet(context, null);
                } else {
                    bad(context, "Required param " + PARAM_NAME);
                }
            } else {
                handleUnAuthorized(context, authInfo);
            }
        });
    }

    @Override
    protected void handlePut(RoutingContext context) {
        log.debug("Handing put account");

        AuthInfo authInfo = AuthManager.getInstance().getAuthInfo(context);
        if (authInfo.isEmpty()) {
            unauthorized(context, AuthManager.DEFAULT_BASIC_REALM); //TODO sync realm name with config value
            return;
        }
        RoleUser user = AuthManager.getInstance().getAuthUser(authInfo);
        log.debug("put with user {}", user);
        if (user == null || !user.hasRole(RoleUser.ROLE_ADMIN)) {
            forbidden(context);
            return;
        }
        LedgerInfo ledgerInfo = LedgerFactory.getDefaultLedger().getInfo();
        String accountName = getAccountName(context);
        JsonObject data = getBodyAsJson(context);
        log.debug("Put data: {}", data);
        Number balance = NumberConversionUtils.toNumber(data.getValue(PARAM_BALANCE, 100d));
        Number minAllowedBalance = NumberConversionUtils.toNumber(data.getValue(PARAM_MIN_ALLOWED_BALANCE, 0d));

        log.debug("Put account {} balance: {}{}", accountName, balance, ledgerInfo.getCurrencyCode());
        LedgerAccountManager accountManager = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton();
        boolean exists = accountManager.hasAccount(accountName);
        LedgerAccount account = exists
                ? accountManager.getAccountByName(accountName)
                : accountManager.create(accountName);
        account.setBalance(balance);
        account.setMinimumAllowedBalance(minAllowedBalance);
        accountManager.store(account);
        response(context, exists ? HttpResponseStatus.OK : HttpResponseStatus.CREATED,
                JsonObjectBuilder.create().from(account));
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

    private void handleAuthorizedGet(RoutingContext context, AuthInfo authInfo) {
        log.info("handleAuthorized {}", authInfo);
        LedgerAccount account = getAccountByName(context);
        JsonObject result;
        if (authInfo == null) {
            result = accountToJsonObject(account);
        } else {
            RoleUser user = AuthManager.getInstance().getAuthUser(authInfo);
            if (user.hasRole(RoleUser.ROLE_ADMIN)) {
                result = JsonObjectBuilder
                        .create().from(account)
                        .put("ledger", LedgerFactory.getDefaultLedger().getInfo().getBaseUri())
                        .get();
            } else {
                result = accountToJsonObject(account);
            }
        }
        response(context, HttpResponseStatus.OK, result);
    }

    private void handleUnAuthorized(RoutingContext context, AuthInfo authInfo) {
        log.info("handleUnAuthorized {}", authInfo);
        forbidden(context);
    }

    private JsonObject accountToJsonObject(LedgerAccount account) {
        return JsonObjectBuilder.create()
                .put("id", account.getUri())
                .put("name", account.getName())
                .put("ledger", LedgerFactory.getDefaultLedger().getInfo().getBaseUri())
                .get();
    }
}
