package org.interledger.ilp.ledger;

import org.interledger.ilp.ledger.account.LedgerAccountManager;
import org.interledger.ilp.ledger.impl.simple.SimpleLedgerAccountManager;

/**
 * Ledger factory.
 *
 * @author mrmx
 */
public class LedgerAccountManagerFactory {

    private static final SimpleLedgerAccountManager instance = new SimpleLedgerAccountManager();

    public static LedgerAccountManager getLedgerAccountManagerSingleton() {
        return instance;
    }
    
    public static LedgerAccountManager createLedgerAccountManager() {
        return new SimpleLedgerAccountManager();
    }
}
