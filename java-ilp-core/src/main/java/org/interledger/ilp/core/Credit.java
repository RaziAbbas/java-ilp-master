package org.interledger.ilp.core;

import javax.money.MonetaryAmount;

public class Credit extends LedgerPartialEntry {
    public final InterledgerPacketHeader ph;

    public Credit(AccountURI uri, MonetaryAmount amount, InterledgerPacketHeader ph){
        super(uri, amount);
        this.ph = ph;
    }
    
}
