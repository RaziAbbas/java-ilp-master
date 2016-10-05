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
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.LedgerInfoFactory;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.interledger.ilp.ledger.account.LedgerAccountManager;
import org.interledger.ilp.ledger.account.LedgerAccountManagerAware;
import org.interledger.ilp.ledger.api.handlers.AccountHandler;
import org.interledger.ilp.ledger.api.handlers.AccountsHandler;
import org.interledger.ilp.ledger.api.handlers.ConnectorsHandler;
import org.interledger.ilp.ledger.api.handlers.HealthHandler;
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
    private LedgerAccountManager ledgerAccountManager;

    //Development configuration namespace:
    enum Dev {
        accounts
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
        LedgerInfo ledgerInfo = LedgerInfoFactory.from(currencyCode);
        LedgerFactory.initialize(ledgerInfo, ledgerName);
        ledger = LedgerFactory.getDefaultLedger();
        //TODO move getLedgerAccountManager to Ledger interface?
        try {
            ledgerAccountManager = ((LedgerAccountManagerAware) ledger).getLedgerAccountManager();
        } catch (Exception ex) {
            throw new ConfigurationException("Preparing ledger instance", ex);
        }
        //Development config
        Optional<Config> devConfig = config.getOptionalConfig(Dev.class);
        if (devConfig.isPresent()) {
            configureDevelopmentEnvirontment(devConfig.get());
        }
    }

    @Override
    protected List<EndpointHandler> getEndpointHandlers(Config config) {
        return Arrays.asList(
                HealthHandler.create(),
                ConnectorsHandler.create(),
                AccountsHandler.create().with(ledgerAccountManager),
                AccountHandler.create().with(ledgerAccountManager)
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
        for(String accountName : accounts) {
            LedgerAccount account = ledgerAccountManager.create(accountName);
            ledgerAccountManager.addAccount(account);
        }
    }

}
