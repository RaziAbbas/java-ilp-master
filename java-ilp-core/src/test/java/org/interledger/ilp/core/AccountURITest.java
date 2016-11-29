package org.interledger.ilp.core;

import static org.junit.Assert.*;
import org.junit.Test;

public class AccountURITest {
    
    @Test
    public void testCtor() {
        final String ledgerUri = "https://ilp.ledger.com";
        final String accountId = "alice";
        final String URI = ledgerUri + "/accounts/" + accountId;
        AccountURI accountUri = new AccountURI(ledgerUri,accountId);
        assertEquals(""+ledgerUri    + " == " + accountUri.getLedgerUri()   , ledgerUri, accountUri.getLedgerUri());
        assertEquals(""+accountId + " == " + accountUri.getAccountId(), accountId, accountUri.getAccountId());
        assertEquals(""+URI + " == " + accountUri.getUri(), URI, accountUri.getUri());
    }
}
