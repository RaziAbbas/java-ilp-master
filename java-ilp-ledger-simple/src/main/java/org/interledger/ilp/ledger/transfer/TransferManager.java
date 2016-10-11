package org.interledger.ilp.ledger.transfer;

import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;

public  interface TransferManager {
    LedgerTransfer getTransferById(TransferID transferId);
    
    boolean transferExists(TransferID transferId);
    
    void createNewRemoteTransfer(LedgerTransfer newTransfer);
    
    void executeLocalTransfer(LedgerTransfer newTransfer);
}
