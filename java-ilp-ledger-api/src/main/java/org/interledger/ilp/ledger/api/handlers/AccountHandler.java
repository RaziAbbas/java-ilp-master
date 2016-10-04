package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import static io.vertx.core.http.HttpMethod.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
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
public class AccountHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(AccountHandler.class);
    private LedgerAccountManager ledgerAccountManager;
    private final static String PARAM_NAME = "name";
    private final static String PARAM_BALANCE = "balance";

    public AccountHandler() {
        super("account", "accounts/:" + PARAM_NAME);
        accept(GET,POST);
    }

    public AccountHandler with(LedgerAccountManager ledgerAccountManager) {
        this.ledgerAccountManager = ledgerAccountManager;
        return this;
    }

    public static AccountHandler create() {
        return new AccountHandler();
    }

    protected void handleGet(RoutingContext context) {
        LedgerAccount account = getAccountByName(context);        
        response(context, HttpResponseStatus.OK, 
                buildJSON("result", Json.encode(account)));
    }

    protected void handlePost(RoutingContext context) {
        String accountName = getAccountName(context);
        JsonObject requestBody = getBodyAsJson(context);
        Double balance = requestBody.getDouble(PARAM_BALANCE, 100d);
        log.debug("Put account {} balance: {}{}", accountName, balance, ledgerAccountManager.getLedgerInfo().getCurrencyCode());
        LedgerAccount account = ledgerAccountManager.create(accountName);
        account.setBalance(balance);
        response(context, HttpResponseStatus.CREATED, 
                buildJSON("result", Json.encode(account)));
    }

    private LedgerAccount getAccountByName(RoutingContext context) {
        String accountName = getAccountName(context);
        log.debug("Get account {}", accountName);
        try {
            return ledgerAccountManager.getAccountByName(accountName);
        } catch (AccountNotFoundException ex) {
            throw new RestEndpointException(HttpResponseStatus.NOT_FOUND, ex.getMessage());
        }

    }

    private String getAccountName(RoutingContext context) {
        String accountName = context.request().getParam(PARAM_NAME);
        if(StringUtils.isBlank(accountName)) {
            throw new RestEndpointException(HttpResponseStatus.NOT_FOUND, accountName);
        }
        return accountName;
    }
}
