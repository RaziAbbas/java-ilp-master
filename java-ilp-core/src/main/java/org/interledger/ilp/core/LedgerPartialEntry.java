package org.interledger.ilp.core;

import javax.money.MonetaryAmount;

/*
 * Represent a part of the Ledger resitry.
 * Code must use subclasses of this class to make it 
 * clear whether its a Debit or a Credit
 */
public abstract class LedgerPartialEntry {
    public final AccountURI account;
    public final MonetaryAmount amount;
    public LedgerPartialEntry(AccountURI uri, MonetaryAmount amount){
        this.account = uri;
        this.amount = amount;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof LedgerPartialEntry))return false;
        return account.equals(((LedgerPartialEntry)other).account) &&
                amount.equals(((LedgerPartialEntry)other).amount);
    }
}
