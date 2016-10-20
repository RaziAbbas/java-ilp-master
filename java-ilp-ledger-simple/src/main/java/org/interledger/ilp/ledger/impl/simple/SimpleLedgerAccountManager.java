package org.interledger.ilp.ledger.impl.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.account.AccountExistsException;
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
    public LedgerAccount create(String name) throws AccountExistsException {
        if (accountMap.containsKey(name)) {
            throw new AccountExistsException("account '"+name+"' already exists");
        }
        return new SimpleLedgerAccount(name, getLedgerInfo().getCurrencyCode());
    }

    @Override
    public void store(LedgerAccount account) {
        accountMap.put(account.getName(), account);
    }

    @Override
    public AccountUri getAccountUri(LedgerAccount account) {
        return new AccountUri(getLedgerInfo().getBaseUri() , account.getName()); 
    }    

    @Override
    public boolean hasAccount(String name) {
        return accountMap.containsKey(name);
    }
    
    @Override
    public LedgerAccount getAccountByName(String name) throws AccountNotFoundException {
        if (!hasAccount(name)) {
            throw new AccountNotFoundException(name);
        }
        return accountMap.get(name);
    }

    @Override
    public Collection<LedgerAccount> getAccounts(int page, int pageSize) {        
        List<LedgerAccount> accounts = new ArrayList<>();
        accountMap.values()
                .stream()
                .filter((LedgerAccount a) -> !a.getName().equals(ILP_HOLD_ACCOUNT))
                .forEach(accounts::add);
        return accounts;
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
