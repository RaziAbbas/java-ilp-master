package org.interledger.ilp.core;

public class AccountUri {
    private final String uri;    
    private final String ledgerUri;    
    private final String accountId;
     
    public AccountUri(String ledgerUri, String accountId) {
        this.ledgerUri = ledgerUri;
        String accountUri = ledgerUri;
        if(!accountUri.endsWith("/")) {
            accountUri += "/";
        }
        this.uri += accountUri + accountId;        
        this.accountId = accountId;
    }

    public String getUri() {
        return uri;
    }

    public String getLedgerUri() {
        return ledgerUri;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof AccountUri))return false;
        return uri.equals(((AccountUri)other).uri);
    }
}
