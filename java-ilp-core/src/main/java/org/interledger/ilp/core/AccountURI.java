package org.interledger.ilp.core;

public class AccountURI {
    public final String URI;
    
    public AccountURI(String URI) {
        // FIXME: Check URI format
        this.URI = URI;
    }
}
