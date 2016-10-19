package org.interledger.ilp.ledger.transfer;

import javax.money.MonetaryAmount;

import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.ConditionURI;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;

public  interface LedgerTransferManager {
    LedgerTransfer getTransferById(TransferID transferId);

    java.util.List<LedgerTransfer> getTransfersByExecutionCondition(ConditionURI condition);

    boolean transferExists(TransferID transferId);
    
    void createNewRemoteILPTransfer(LedgerTransfer newTransfer);

    // void executeLocalTransfer(AccountUri from, AccountUri to, MonetaryAmount amount);

    void executeLocalTransfer(LedgerTransfer transfer);

    void executeRemoteILPTransfer(LedgerTransfer transfer);

    void abortRemoteILPTransfer(LedgerTransfer transfer);

}
