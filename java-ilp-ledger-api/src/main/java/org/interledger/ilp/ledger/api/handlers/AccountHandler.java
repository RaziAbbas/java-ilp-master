package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import static io.vertx.core.http.HttpMethod.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.interledger.ilp.common.api.ProtectedResource;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;
import org.interledger.ilp.common.api.util.JsonObjectBuilder;
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
public class AccountHandler extends RestEndpointHandler implements ProtectedResource {

    private static final Logger log = LoggerFactory.getLogger(AccountHandler.class);
//    private static LedgerAccountManager ledgerAccountManager;
    private final static String PARAM_NAME = "name";
    private final static String PARAM_BALANCE = "balance";
    
    public AccountHandler() {
        super("account", "accounts/:" + PARAM_NAME);
        accept(GET, POST);
    }

    public AccountHandler with(LedgerAccountManager ledgerAccountManager) {
//    	it (this.ledgerAccountManager)
//        this.ledgerAccountManager = ledgerAccountManager;
        return this;
    }

    public static AccountHandler create() {
        return new AccountHandler();
    }

    protected void handleGet(RoutingContext context) {
        LedgerAccount account = getAccountByName(context);
        // TODO: Ussually ledgerURL will be a constant that can be fetch from config params.
        //       No need to recalculate each time. Could it be the case that the server is configured with
        //       different URL??? In such case there is no other way that fetchint it from the request.
        String absoluteURI = context.request().absoluteURI();
        String ledgerURL = absoluteURI.substring(0, absoluteURI.indexOf(context.request().path()));
        JsonObject result = JsonObjectBuilder.create()
                .from(account)
                .put("ledger", ledgerURL)
                .get();        
        response(context, HttpResponseStatus.OK,result);
    }

    protected void handlePost(RoutingContext context) {
    	LedgerInfo ledgerInfo = LedgerFactory.getLedger().getInfo();
        String accountName = getAccountName(context);
        JsonObject requestBody = getBodyAsJson(context);
        Double balance = requestBody.getDouble(PARAM_BALANCE, 100d);
        ledgerInfo.getCurrencyCode();

        log.debug("Put account {} balance: {}{}", accountName, balance, ledgerInfo.getCurrencyCode());
        LedgerAccount account = LedgerAccountManagerFactory.getAccountManagerSingleton().create(accountName);
        account.setBalance(balance);
        response(context, HttpResponseStatus.CREATED,
                buildJSON("result", Json.encode(account)));
    }

    private static LedgerAccount getAccountByName(RoutingContext context) {
        String accountName = getAccountName(context);
        log.debug("Get account {}", accountName);
        try {
            return LedgerAccountManagerFactory.getAccountManagerSingleton().getAccountByName(accountName);
        } catch (AccountNotFoundException ex) {
            throw new RestEndpointException(HttpResponseStatus.NOT_FOUND, ex.getMessage());
        }

    }

    private static String getAccountName(RoutingContext context) {
        String accountName = context.request().getParam(PARAM_NAME);
        if (StringUtils.isBlank(accountName)) {
            throw new RestEndpointException(HttpResponseStatus.NOT_FOUND, accountName);
        }
        return accountName;
    }
}
