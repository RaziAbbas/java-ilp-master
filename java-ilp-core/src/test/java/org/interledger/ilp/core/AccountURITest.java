package org.interledger.ilp.core;

import static org.junit.Assert.*;
//import org.junit.Before;
//import org.junit.Ignore;
import org.junit.Test;

public class AccountURITest {
    
    @Test
    public void testSend() {
        final String ledger = "https://ilp.ledger.com";
        final String accountId = "alice";
        final String URI = ledger + "/accounts/" + accountId;
        AccountUri accURI = new AccountUri(URI);
        assertEquals(""+ledger    + " == " + accURI.ledger   , ledger, accURI.ledger);
        assertEquals(""+accountId + " == " + accURI.accoundId, accountId, accURI.accoundId);
    }
}
