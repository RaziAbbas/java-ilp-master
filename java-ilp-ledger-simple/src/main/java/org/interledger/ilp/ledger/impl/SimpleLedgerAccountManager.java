package org.interledger.ilp.ledger.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.interledger.ilp.core.AccountUri;
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

    private Map<AccountUri, LedgerAccount> accountMap;

    public SimpleLedgerAccountManager() {
        accountMap = new HashMap<AccountUri, LedgerAccount>();
    }

    @Override
    public LedgerAccount create(AccountUri name) {
        return new SimpleLedgerAccount(name, LedgerFactory.getDefaultLedger().getInfo().getCurrencyCode());
    }

    @Override
    public void addAccount(LedgerAccount account) {
        accountMap.put(account.getAccountUri(), account);
    }

    @Override
    public LedgerAccount getAccountByName(AccountUri name) throws AccountNotFoundException {
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
