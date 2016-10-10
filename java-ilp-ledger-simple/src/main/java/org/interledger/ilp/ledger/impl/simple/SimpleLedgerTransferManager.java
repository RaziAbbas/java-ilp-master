package org.interledger.ilp.ledger.impl.simple;

import java.util.HashMap;
import java.util.Map;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
/**
 * Simple in-memory {@code SimpleLedgerTransferManager}.
 *
 * @author earizon
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
