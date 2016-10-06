package org.interledger.ilp.core;

public class AccountURI {
    public final String URI;
    public final String ledger;
    public final String accoundId;
    
    private final String ACCOUNT_DELIMITER = "/accounts/";

    public AccountURI(String URI) {
        // FIXME: Check URI format
        int idx0 = URI.indexOf(ACCOUNT_DELIMITER),
            idx1 = idx0 + ACCOUNT_DELIMITER.length();
        this.URI = URI;
        this.ledger = URI.substring(0, idx0);
        this.accoundId = URI.substring(idx1);
    }
}
