package org.interledger.ilp.ledger.impl.simple;

import java.util.Collection;
import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.ledger.Currencies;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.LedgerInfoBuilder;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.interledger.ilp.ledger.account.LedgerAccountManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mrmx
 */
public class SimpleLedgerAccountManagerTest {

    LedgerAccountManager instance;

    AccountUri aliceURI = new AccountUri("https://ledger1/accounts", "alice");
    AccountUri bobURI = new AccountUri("https://ledger2/accounts", "bob");
    

    @BeforeClass
    public static void init() {
        LedgerInfo ledgerInfo = new LedgerInfoBuilder()
            .setCurrency(Currencies.EURO)
            .setBaseUri("https://ledger.example")
            .build();        
        LedgerFactory.initialize(ledgerInfo, "test-ledger");
    }
    
    @Before
    public void setUp() {        
        instance = LedgerAccountManagerFactory.createLedgerAccountManager();
    }

    /**
     * Test of create method, of class SimpleLedgerAccountManager.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        LedgerAccount result = instance.create(aliceURI.getAccountId());
        assertNotNull(result);
        assertEquals(aliceURI, instance.getAccountUri(result));
        assertEquals("EUR", result.getCurrencyCode());
    }

    /**
     * Test of addAccounts method, of class SimpleLedgerAccountManager.
     */
    @Test
    public void testAddAccounts() {
        System.out.println("addAccounts");
        assertEquals(0, instance.getTotalAccounts());
        instance.addAccount(new SimpleLedgerAccount("alice", "EUR"));
        instance.addAccount(new SimpleLedgerAccount("bob", "EUR"));
        assertEquals(2, instance.getTotalAccounts());
    }

    /**
     * Test of addAccount method, of class SimpleLedgerAccountManager.
     */
    @Test
    public void testAddAccount() {
        System.out.println("addAccount");
        assertEquals(0, instance.getTotalAccounts());
        instance.addAccount(new SimpleLedgerAccount("alice", "EUR"));
        assertEquals(1, instance.getTotalAccounts());
    }

    /**
     * Test of getAccountByName method, of class SimpleLedgerAccountManager.
     */
    @Test
    public void testGetAccountByName() {
        System.out.println("getAccountByName");
        LedgerAccount bob = instance.create("bob");
        instance.addAccount(bob);
        instance.addAccount(instance.create("alice"));
        assertEquals(2, instance.getTotalAccounts());
        LedgerAccount result = instance.getAccountByName("bob");
        assertEquals(bob, result);
    }

    /**
     * Test of getAccounts method, of class SimpleLedgerAccountManager.
     */
    @Test
    public void testGetAccounts() {
        System.out.println("testGetAccounts");
        LedgerAccount bob = instance.create("bob");
        instance.addAccount(bob);
        instance.addAccount(instance.create("alice"));
        assertEquals(2, instance.getTotalAccounts());
        Collection<LedgerAccount> result = instance.getAccounts(1, 1);
        assertEquals(2, result.size());
    }

}
