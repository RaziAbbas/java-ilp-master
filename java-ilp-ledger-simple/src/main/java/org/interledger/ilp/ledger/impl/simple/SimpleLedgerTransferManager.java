package org.interledger.ilp.ledger.impl.simple;

import java.util.HashMap;
import java.util.Map;

import javax.money.MonetaryAmount;

import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.Credit;
import org.interledger.ilp.core.Debit;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.account.LedgerAccountManager;
import org.interledger.ilp.ledger.transfer.LedgerTransferManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/**
 * Simple in-memory {@code SimpleLedgerTransferManager}.
 *
 * @author earizon
 */
public class SimpleLedgerTransferManager implements LedgerTransferManager /* FIXME TODO implements LedgerTransferManager, LedgerTransferManagerFactory */{

    private Map<TransferID, LedgerTransfer> transferMap = 
        new HashMap<TransferID, LedgerTransfer>();// In-memory database of pending/executed/cancelled transfers

    private static SimpleLedgerTransferManager singleton = new SimpleLedgerTransferManager();

    private static final AccountUri HOLDS_URI = AccountUri.buildFromURI(
            LedgerAccountManagerFactory.getLedgerAccountManagerSingleton().
                getHOLDAccountILP().getUri());;

    // Make default constructor private to avoid instantiating new classes.
    private SimpleLedgerTransferManager() {}

    public static LedgerTransferManager getSingleton() {
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
        System.out.println("transferExists transferId:"+transferId.transferID);
        boolean result = transferMap.containsKey(transferId);
        System.out.println("transferExists result:"+result);
        return result;
    }

    @Override
    public void createNewRemoteILPTransfer(LedgerTransfer newTransfer) {
        System.out.println("createNewRemoteTransfer newTransfer:"+newTransfer.getTransferID().transferID);

        // FIXME: If accounts are both locals the execute and forget.
        if (transferExists(newTransfer.getTransferID())) {
            throw new RuntimeException("trying to create new transfer "
                    + "but transferID '"+newTransfer.getTransferID()+"'already registrered. "
                    + "Check transfer with SimpleLedgerTransferManager.transferExists before invoquing this function");
        }
        transferMap.put(newTransfer.getTransferID(), newTransfer);
        // PUT Money on-hold:
        for (Debit debit : newTransfer.getDebits()) {
            executeLocalTransfer(debit.account, HOLDS_URI, debit.amount);
        }
    }

    @Override
    public void executeLocalTransfer(AccountUri sender, AccountUri recipient, MonetaryAmount amount) {
        // FIXME: LOG local transfer execution.
        LedgerAccountManager accManager = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton();
        accManager.getAccountByName(sender   .getAccountId()).debit (amount);
        accManager.getAccountByName(recipient.getAccountId()).credit(amount);
    }

    @Override
    public void executeRemoteILPTransfer(LedgerTransfer transfer) {
        // DisburseFunds:
        for (Credit debit : transfer.getCredits()) {
            executeLocalTransfer(HOLDS_URI, debit.account, debit.amount);
        }
    }

    @Override
    public void abortRemoteILPTransfer(LedgerTransfer transfer) {
        // Return Held Funds
        for (Debit debit : transfer.getDebits()) {
            executeLocalTransfer(HOLDS_URI, debit.account, debit.amount);
        }
    }

}
