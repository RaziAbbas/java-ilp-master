package org.interledger.ilp.ledger.impl.simple;

import java.util.HashMap;
import java.util.Map;

import javax.money.MonetaryAmount;

import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.account.LedgerAccountManager;
import org.interledger.ilp.ledger.transfer.TransferManager;
/**
 * Simple in-memory {@code SimpleLedgerTransferManager}.
 *
 * @author earizon
 */
public class SimpleLedgerTransferManager implements TransferManager /* FIXME TODO implements LedgerTransferManager, LedgerTransferManagerFactory */{

    private Map<TransferID, LedgerTransfer> transferMap = 
        new HashMap<TransferID, LedgerTransfer>();// In-memory database of pending/executed/cancelled transfers

    private static SimpleLedgerTransferManager singleton = new SimpleLedgerTransferManager();

    // Make default constructor private to avoid instantiating new classes.
    private SimpleLedgerTransferManager() {}

    public static TransferManager getSingleton() {
        return singleton;
    }

    @Override
    public LedgerTransfer getTransferById(TransferID transferId) {
        LedgerTransfer result = transferMap.get(transferId);
        if (result == null) {
            throw new RuntimeException("No transaction found for transferId "+ transferId.toString());
        }
        return result;
    }
    
    @Override
    public boolean transferExists(TransferID transferId) {
        return transferMap.containsKey(transferId);
    }
    
    @Override
    public void createNewRemoteTransfer(LedgerTransfer newTransfer) {
        // FIXME: If accounts are both locals the execute and forget.
        if (transferExists(newTransfer.getTransferID())) {
            throw new RuntimeException("trying to create new transfer "
                    + "but transferID '"+newTransfer.getTransferID()+"'already registrered. "
                    + "Check transfer with SimpleLedgerTransferManager.transferExists before invoquing this function");
        }
        transferMap.put(newTransfer.getTransferID(), newTransfer);
        // FIXME: Notify ilp-connector
        
    }

    @Override
    public void executeLocalTransfer(AccountUri sender, AccountUri recipient, MonetaryAmount amount) {
        LedgerAccountManager accManager = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton();
        accManager.getAccountByName(sender   .getAccountId()).debit (amount);
        accManager.getAccountByName(recipient.getAccountId()).credit(amount);
        
        
    }

}
