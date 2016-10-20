package org.interledger.ilp.ledger.account;

import java.util.Collection;
import org.interledger.ilp.core.AccountUri;

/**
 * Defines an account manager.
 *
 * @author mrmx
 */
public interface LedgerAccountManager {

    LedgerAccount create(String name) throws AccountExistsException;

    int getTotalAccounts();

    void store(LedgerAccount account);

    boolean hasAccount(String name);

    LedgerAccount getAccountByName(String name) throws AccountNotFoundException;

    Collection<LedgerAccount> getAccounts(int page, int pageSize);

    AccountUri getAccountUri(LedgerAccount account);

    LedgerAccount getHOLDAccountILP();
}
