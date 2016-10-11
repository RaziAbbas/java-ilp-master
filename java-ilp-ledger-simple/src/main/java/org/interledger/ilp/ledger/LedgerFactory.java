package org.interledger.ilp.ledger;

import org.interledger.ilp.core.Ledger;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.ledger.impl.simple.SimpleLedger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ledger factory.
 *
 * @author mrmx
 */
public class LedgerFactory {
    private static final Logger log = LoggerFactory.getLogger(LedgerFactory.class);
    
    private static SimpleLedger defaultLedgerSingleton;

    public static void initialize(LedgerInfo ledgerInfo, String ledgerName) {
        if (defaultLedgerSingleton != null) {
            log.warn("{} already initialized",LoggerFactory.class.getSimpleName());
        }
        defaultLedgerSingleton = new SimpleLedger(ledgerInfo, ledgerName);
    }

    public static Ledger getDefaultLedger() {
        if (defaultLedgerSingleton == null) {
            throw new RuntimeException("Default ledger is null. At startup " + LedgerFactory.class.getSimpleName()
                    + ".initialize(LedgerInfo ledgerInfo,String ledgerName) must be called to initialize the factory");
        }
        return defaultLedgerSingleton;
    }
}
