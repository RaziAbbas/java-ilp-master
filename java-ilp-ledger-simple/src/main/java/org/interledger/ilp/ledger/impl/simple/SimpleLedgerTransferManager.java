package org.interledger.ilp.ledger.impl.simple;

import java.util.HashMap;
import java.util.Map;

import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
//import org.interledger.ilp.ledger.LedgerFactory;
//import org.interledger.ilp.ledger.account.AccountNotFoundException;
//import org.interledger.ilp.ledger.account.LedgerAccount;
//import org.interledger.ilp.ledger.account.LedgerAccountManager;

/**
 * Simple in-memory {@code LedgerAccountManager}.
 *
 * @author mrmx
 */
public class SimpleLedgerTransferManager /* FIXME TODO implements LedgerTransferManager, LedgerTransferManagerFactory */{

    private Map<TransferID, LedgerTransfer> transferMap = 
        new HashMap<TransferID, LedgerTransfer>();// In-memory database of pending/executed/cancelled transfers

    private static SimpleLedgerTransferManager singleton = new SimpleLedgerTransferManager();

    private SimpleLedgerTransferManager() { }

    public static SimpleLedgerTransferManager getSingletonInstance() {
        return singleton;
    }

    public LedgerTransfer getTransferById(TransferID transferId) {
        LedgerTransfer result = transferMap.get(transferId);
        if (result == null) {
            throw new RuntimeException("No transaction found for transferId "+ transferId.toString());
        }
        return result;
    }
    
    public boolean transferExists(TransferID transferId) {
        return transferMap.containsKey(transferId);
    }
    
}
