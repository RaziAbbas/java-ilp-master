package org.interledger.ilp.ledger;

import org.interledger.ilp.core.Ledger;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.ledger.impl.SimpleLedger;

/**
 * Ledger factory.
 *
 * @author mrmx
 */
public class LedgerFactory {

    private static SimpleLedger defaultLedgerSingleton;

    public static void initialize(LedgerInfo ledgerInfo, String ledgerName) {
        if (defaultLedgerSingleton != null) {
            return;
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
