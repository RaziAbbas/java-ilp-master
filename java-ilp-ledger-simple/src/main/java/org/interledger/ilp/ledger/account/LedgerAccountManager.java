package org.interledger.ilp.ledger.account;

import java.util.Collection;
//import org.interledger.ilp.core.LedgerInfo;

import org.interledger.ilp.core.AccountUri;

/**
 * Defines account management.
 *
 * @author mrmx
 */
public interface LedgerAccountManager {

    LedgerAccount create(AccountUri name);

    int getTotalAccounts();

    void addAccount(LedgerAccount account);

    LedgerAccount getAccountByName(AccountUri name) throws AccountNotFoundException;

    Collection<LedgerAccount> getAccounts(int page, int pageSize);
    
    
}
