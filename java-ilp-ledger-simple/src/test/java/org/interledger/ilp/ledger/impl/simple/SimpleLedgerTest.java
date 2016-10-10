package org.interledger.ilp.ledger.impl.simple;

import org.interledger.cryptoconditions.Fulfillment;
import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.ConditionURI;
import org.interledger.ilp.core.DTTM;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.LedgerTransferRejectedReason;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.core.TransferStatus;
import org.interledger.ilp.core.events.LedgerEventHandler;
import org.interledger.ilp.ledger.Currencies;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.interledger.ilp.ledger.account.LedgerAccountManager;
import org.javamoney.moneta.Money;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Simple ledger tests
 *
 * @author mrmx
 */
public class SimpleLedgerTest {

    Currencies CURRENCY = Currencies.EURO;
    SimpleLedger instance;
    
    final String ALICE = "alice";
    final String BOB   = "bob";

    @Before
    public void setUp() {
        instance = new SimpleLedger(CURRENCY, "test");
    }

    /**
     * Test of getInfo method, of class SimpleLedger.
     */
    @Test
    public void testGetInfo() {
        System.out.println("getInfo");
        LedgerInfo result = instance.getInfo();
        assertNotNull(result);
    }

    /**
     * Test of send method, of class SimpleLedger.
     */
    // FIXME: Recheck test
    // @Test
    public void testSend() {
        System.out.println("send");
        TransferID transferID = new TransferID("3a2a1d9e-8640-4d2d-b06c-84f2cd613204");

        LedgerAccount alice = new SimpleLedgerAccount(ALICE, CURRENCY.code()).setBalance(100);
        LedgerAccount bob   = new SimpleLedgerAccount(BOB  , CURRENCY.code()).setBalance(100);
        LedgerAccountManager accountManager = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton();
        accountManager.addAccount(alice);
        accountManager.addAccount(bob);
        LedgerTransfer transfer = 
                new SimpleLedgerTransfer(transferID, 
                    accountManager.getAccountUri(alice), new AccountUri("http://ledger",BOB), 
                    Money.of(10, CURRENCY.code()), new ConditionURI("cc:execution"),
                    new ConditionURI("cc:cancelation"),
                    new DTTM(""), new DTTM(""),
                    "" /* data*/, "" /* noteToSelf*/, TransferStatus.PROPOSED );
        instance.send(transfer);
        assertEquals(90, accountManager.getAccountByName(ALICE).getBalance().getNumber().intValue());
        assertEquals(110, accountManager.getAccountByName(BOB).getBalance().getNumber().intValue());
    }

    /**
     * Test of rejectTransfer method, of class SimpleLedger.
     */
    @Ignore
    @Test
    public void testRejectTransfer() {
        System.out.println("rejectTransfer");
        LedgerTransfer transfer = null;
        LedgerTransferRejectedReason reason = null;
        instance.rejectTransfer(transfer, reason);
        fail("The test case is a prototype.");
    }

    /**
     * Test of fulfillCondition method, of class SimpleLedger.
     */
    @Ignore
    @Test
    public void testFulfillCondition() {
        System.out.println("fulfillCondition");
        Fulfillment fulfillment = null;
        instance.fulfillCondition(fulfillment);
        fail("The test case is a prototype.");
    }

    /**
     * Test of registerEventHandler method, of class SimpleLedger.
     */
    @Ignore
    @Test
    public void testRegisterEventHandler() {
        System.out.println("registerEventHandler");
        LedgerEventHandler<?> handler = null;
        instance.registerEventHandler(handler);
        fail("The test case is a prototype.");
    }

}
