package org.interledger.ilp.ledger.transfer;

import javax.money.MonetaryAmount;

import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;

public  interface LedgerTransferManager {
    LedgerTransfer getTransferById(TransferID transferId);
    
    boolean transferExists(TransferID transferId);
    
    void createNewRemoteTransfer(LedgerTransfer newTransfer);
    
    void executeLocalTransfer(AccountUri from, AccountUri to, MonetaryAmount amount);
    
    void executeTransfer(LedgerTransfer transfer);

    void abortTransfer(LedgerTransfer transfer);

}
