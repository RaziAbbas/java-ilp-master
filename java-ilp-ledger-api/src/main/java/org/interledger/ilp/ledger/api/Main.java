package org.interledger.ilp.ledger.api;

import com.google.common.base.Optional;
import io.vertx.ext.web.Router;
import java.util.Arrays;
import java.util.List;
import org.interledger.ilp.common.api.AbstractMainEntrypointVerticle;
import org.interledger.ilp.common.api.handlers.EndpointHandler;
import org.interledger.ilp.common.api.handlers.IndexHandler;
import org.interledger.ilp.common.api.util.VertxRunner;
import org.interledger.ilp.common.config.Config;
import static org.interledger.ilp.common.config.Key.*;
import org.interledger.ilp.common.config.core.Configurable;
import org.interledger.ilp.common.config.core.ConfigurationException;
import org.interledger.ilp.core.Ledger;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.LedgerInfoBuilder;
import org.interledger.ilp.ledger.account.LedgerAccountManager;
import org.interledger.ilp.ledger.api.handlers.AccountHandler;
import org.interledger.ilp.ledger.api.handlers.AccountsHandler;
import org.interledger.ilp.ledger.api.handlers.ConnectorsHandler;
import org.interledger.ilp.ledger.api.handlers.HealthHandler;
import org.interledger.ilp.ledger.api.handlers.TransferHandler;
import org.interledger.ilp.ledger.api.handlers.TransferWSEventHandler;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vertx main entry point.
 *
 * @author mrmx
 */
public class Main extends AbstractMainEntrypointVerticle implements Configurable {

    // TODO: FIXME This server must implement the Wallet expected (REST and WebSocket?)
    //   interface as well as the js-ilp-connector (js-ilp-plugin-bells\src\lib\plugin.js) one.
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String DEFAULT_LEDGER_NAME = "ledger-simple";

    private String ilpPrefix;
    private Ledger ledger;

    //Development configuration namespace:
    enum Dev {
        uri,
        accounts,
        balance,
        admin, disabled,
        connector
    }

    // TODO: Move to the ledger-simple. The main is not part of the (reusable) API.
    public static void main(String[] args) {
        VertxRunner.run(Main.class);

    }

    @Override
    public void start() throws Exception {
        log.info("Starting ILP ledger api server");
        super.start();
    }

    @Override
    public void configure(Config config) throws ConfigurationException {
        ilpPrefix = config.getString(LEDGER, ILP, PREFIX);
        String ledgerName = config.getString(DEFAULT_LEDGER_NAME, LEDGER, NAME);
        String currencyCode = config.getString(LEDGER, CURRENCY, CODE);
        String baseUri = getServerPublicURL().toString();
        //Development config
        Optional<Config> devConfig = config.getOptionalConfig(Dev.class);
        if (devConfig.isPresent()) {
            Config dev = devConfig.get();
            //Override baseUri to match 5-bells integration tests:
            baseUri = dev.getString(baseUri, Dev.uri);
        }
        LedgerInfo ledgerInfo = new LedgerInfoBuilder()
                .setBaseUri(baseUri)
                .setCurrencyCodeAndSymbol(currencyCode)
                //TODO precission and scale
                .build();
        LedgerFactory.initialize(ledgerInfo, ledgerName);
        ledger = LedgerFactory.getDefaultLedger();
        //Development config
        if (devConfig.isPresent()) {
            configureDevelopmentEnvirontment(devConfig.get());
        }
    }

    @Override
    protected List<EndpointHandler> getEndpointHandlers(Config config) {
        return Arrays.asList(
                HealthHandler.create(),
                ConnectorsHandler.create(),
                AccountsHandler.create(),
                AccountHandler.create(),
                TransferHandler.create(),
                TransferWSEventHandler.create()
        );
    }

    @Override
    protected void initIndexHandler(Router router, IndexHandler indexHandler) {
        super.initIndexHandler(router, indexHandler);
        LedgerInfo ledgerInfo = ledger.getInfo();
        indexHandler
                .put("ilp_prefix", ilpPrefix)
                .put("ilp_prefix", ilpPrefix)
                .put("currency_code", ledgerInfo.getCurrencyCode())
                .put("currency_symbol", ledgerInfo.getCurrencySymbol())
                .put("precision", ledgerInfo.getPrecision())
                .put("scale", ledgerInfo.getScale());
    }

    private void configureDevelopmentEnvirontment(Config config) {
        log.info("Preparing development environment");
        List<String> accounts = config.getStringList(Dev.accounts);
        LedgerAccountManager ledgerAccountManager = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton();
        for(String accountName : accounts) {
            SimpleLedgerAccount account = (SimpleLedgerAccount) ledgerAccountManager.create(accountName);
            Config accountConfig = config.getConfig(accountName);
            account.setBalance(accountConfig.getInt(0, Dev.balance));
            if (accountConfig.getBoolean(false, Dev.admin)) {
                account.setAdmin(true);
            }
            account.setDisabled(accountConfig.getBoolean(false, Dev.disabled));
            account.setConnector(accountConfig.getString((String)null, Dev.connector));
            ledgerAccountManager.store(account);
        }
    }

}
