package org.interledger.ilp.core;

import javax.money.MonetaryAmount;

public class Credit extends LedgerPartialEntry {
    public Credit(AccountUri uri, MonetaryAmount amount){
        super(uri, amount);
    }
}
