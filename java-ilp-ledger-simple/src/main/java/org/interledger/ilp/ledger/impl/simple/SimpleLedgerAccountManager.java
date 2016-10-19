package org.interledger.ilp.ledger.impl.simple;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.LedgerInfo;
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
    private Map<String, LedgerAccount> accountMap;
    private static final String ILP_HOLD_ACCOUNT = "@@ILP_HOLDS@@"; 

    public SimpleLedgerAccountManager() {
        accountMap = new TreeMap<String, LedgerAccount>();
    }
    
    @Override
    public LedgerAccount create(String name) {
        if (accountMap.containsKey(name)) {
            throw new RuntimeException("account '"+name+"' already exists");
        }
        return new SimpleLedgerAccount(name, getLedgerInfo().getCurrencyCode());
    }

    @Override
    public void addAccount(LedgerAccount account) {
        accountMap.put(account.getName(), account);
    }

    @Override
    public AccountUri getAccountUri(LedgerAccount account) {
        return new AccountUri(getLedgerInfo().getBaseUri() , account.getName()); 
    }    

    @Override
    public LedgerAccount getAccountByName(String name) throws AccountNotFoundException {
        if (!accountMap.containsKey(name)) {
            throw new AccountNotFoundException(name);
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
    
    private LedgerInfo getLedgerInfo() {
        return LedgerFactory.getDefaultLedger().getInfo();
    }
    
    @Override
    public LedgerAccount getHOLDAccountILP() {
        if (accountMap.containsKey(ILP_HOLD_ACCOUNT)) { return accountMap.get(ILP_HOLD_ACCOUNT); }
        return create(ILP_HOLD_ACCOUNT);
    }


}
