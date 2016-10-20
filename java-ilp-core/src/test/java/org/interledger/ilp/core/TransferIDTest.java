package org.interledger.ilp.core;

//import static org.junit.Assert.*;
//import org.junit.Before;
//import org.junit.Ignore;
import org.junit.Test;

public class TransferIDTest {
    // TODO: Add more test (compare objects, ...)
    @Test
    public void testCreateTransferID() {
        new TransferID("http://ledger:3000/transfers/3a2a1d9e-8640-4d2d-b06c-84f2cd613204");
    }

    @Test (expected = RuntimeException.class)
    public void testCreateTransferIDWithWrongURI() {
        new TransferID("3a2a1d9e-8640-4d2d-b06c-84f2cd613204");
    }

    @Test(expected=RuntimeException.class)
    public void testCreateDTTMWrongFormat() {
        new TransferID("1234");
    }
}
