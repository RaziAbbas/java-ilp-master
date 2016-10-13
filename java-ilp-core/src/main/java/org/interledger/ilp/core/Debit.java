package org.interledger.ilp.core;

import javax.money.MonetaryAmount;

public class Debit extends LedgerPartialEntry {
    public Debit(AccountUri uri, MonetaryAmount amount){
        super(uri, amount);
    }
}
