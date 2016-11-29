package org.interledger.ilp.core;
/*
 * This class supposes an AccountUri has the format
 *    https://ledger/accounts/alice
 *    ^------------^          ^---^ 
 *       ledgerUri         accountId
 *    Were /accounts/ is always an string constant
 */
public class AccountURI {
    private final String uri; // FIXME:? Replace string by java.io.URL
    private final String ledgerUri; // TODO: recheck. duplicated info in uri/ledgerUri? 
    private final String accountId;
    final private static String  ACCOUNTS = "/accounts";
     
    public AccountURI(String ledgerUri, String accountId) {
        if (ledgerUri == null) { throw new RuntimeException("ledgerUri null at AccountUri constructor"); }
        if (accountId == null) { throw new RuntimeException("accountId null at AccountUri constructor"); }
        if(ledgerUri.indexOf(ACCOUNTS) >= 0) {
            throw new RuntimeException("ledgerUri is not expected to contain the '"+ACCOUNTS+"' substring");
        }
        if(ledgerUri.endsWith("/")) { ledgerUri = ledgerUri.substring(0, ledgerUri.length()-1); }
        this.ledgerUri = ledgerUri;
        this.accountId = accountId;
        this.uri = ledgerUri + ACCOUNTS + "/" + accountId; 
    }
    
    public static AccountURI buildFromURI(String uri) {
        // Parse sAccount = http..../accounts/alice
        int idx = uri.indexOf(ACCOUNTS);
        if (idx < 0 ) {
            throw new RuntimeException(uri + "couldn't be parsed as a valid account");
        }
        String ledgerUri = uri.substring( 0, idx);
        String accountId = uri.substring( idx + ACCOUNTS.length() + 1);
        return new AccountURI(ledgerUri, accountId);
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
        if (!(other instanceof AccountURI))return false;
        return uri.equals(((AccountURI)other).uri);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("[");        
        sb.append("uri:").append(uri);
        sb.append("]");
        return sb.toString();
    }
}
