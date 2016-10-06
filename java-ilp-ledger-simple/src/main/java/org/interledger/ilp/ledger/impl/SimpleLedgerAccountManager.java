package org.interledger.ilp.ledger.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.interledger.ilp.core.AccountURI;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.account.AccountNotFoundException;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.interledger.ilp.ledger.account.LedgerAccountManager;

/**
 * Simple in-memory {@code LedgerAccountManager}.
 *
 * @author mrmx
 */
public class SimpleLedgerAccountManager implements LedgerAccountManager {

    private Map<AccountURI, LedgerAccount> accountMap;

    public SimpleLedgerAccountManager() {
        accountMap = new HashMap<AccountURI, LedgerAccount>();
    }

    @Override
    public LedgerAccount create(AccountURI name) {
        return new SimpleLedgerAccount(name, LedgerFactory.getDefaultLedger().getInfo().getCurrencyCode());
    }

    @Override
    public void addAccount(LedgerAccount account) {
        accountMap.put(account.getName(), account);
    }

    @Override
    public LedgerAccount getAccountByName(AccountURI name) throws AccountNotFoundException {
        if (!accountMap.containsKey(name)) {
            throw new AccountNotFoundException(name.toString());
        }
        return accountMap.get(name);
    }

    @Override
    public Collection<LedgerAccount> getAccounts(int page, int pageSize) {
        // TODO
        return accountMap.values();
    }

    @Override
    public int getTotalAccounts() {
        return accountMap.size();
    }

}
