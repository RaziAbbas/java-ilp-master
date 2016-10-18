package org.interledger.ilp.ledger.impl.simple;

import java.util.HashMap;
import java.util.Map;

import javax.money.MonetaryAmount;

import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.ConditionURI;
import org.interledger.ilp.core.Credit;
import org.interledger.ilp.core.DTTM;
import org.interledger.ilp.core.Debit;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.core.TransferStatus;
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
    public void createNewRemoteTransfer(LedgerTransfer newTransfer) {
        System.out.println("createNewRemoteTransfer newTransfer:"+newTransfer.getTransferID().transferID);

        // FIXME: If accounts are both locals the execute and forget.
        if (transferExists(newTransfer.getTransferID())) {
            throw new RuntimeException("trying to create new transfer "
                    + "but transferID '"+newTransfer.getTransferID()+"'already registrered. "
                    + "Check transfer with SimpleLedgerTransferManager.transferExists before invoquing this function");
        }
        transferMap.put(newTransfer.getTransferID(), newTransfer);
        // FIXME: Notify ilp-connector
        /*
         *  function holdFunds (transfer, transaction) {
         *    return Promise.all(transfer.debits.map((debit) => {
         *      return Promise.all([
         *        _adjustBalance(debit.account, -debit.amount, transaction),
         *        _adjustBalance('hold', debit.amount, transaction),
         *        _insertEntryByName(debit.account, transfer.id, transaction)
         *      ])
         *    }))
         *  }
         */
    }

    @Override
    public void executeLocalTransfer(AccountUri sender, AccountUri recipient, MonetaryAmount amount) {
        LedgerAccountManager accManager = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton();
        accManager.getAccountByName(sender   .getAccountId()).debit (amount);
        accManager.getAccountByName(recipient.getAccountId()).credit(amount);
    }

    @Override
    public void executeTransfer(LedgerTransfer transfer) {
        /*
        public SimpleLedgerTransfer(TransferID transferID, 
                Debit[] debit_list, Credit[] credit_list, 
                ConditionURI URIExecutionCond, 
                ConditionURI URICancelationCond, DTTM DTTM_expires, DTTM DTTM_proposed,
                String data, String noteToSelf, TransferStatus transferStatus )*/
        /* const holds = require('../lib/holds')
         *
         * function * executeTransfer (transaction, transfer, fulfillment) {
         *    yield holds.disburseFunds(transfer, transaction)
         *    updateState(transfer, transferStates.TRANSFER_STATE_EXECUTED)
         *    yield fulfillments.upsertFulfillment(fulfillment, {transaction})
         *  }
         *  
         *  function disburseFunds (transfer, transaction) {
         *    return Promise.all(transfer.credits.map((credit) => {
         *      return Promise.all([
         *        _adjustBalance('hold', -credit.amount, transaction),
         *        _adjustBalance(credit.account, credit.amount, transaction),
         *        _insertEntryByName(credit.account, transfer.id, transaction)
         *      ])
         *    }))
         *  }
         *  
         */
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void abortTransfer(LedgerTransfer transfer) {
        /*
         * function * cancelTransfer (transaction, transfer, fulfillment) {
         *   if (transfer.state === transferStates.TRANSFER_STATE_PREPARED) {
         *     yield holds.returnHeldFunds(transfer, transaction)
         *   }
         *   yield fulfillments.upsertFulfillment(fulfillment, {transaction})
         *   transfer.rejection_reason = 'cancelled'
         *   updateState(transfer, transferStates.TRANSFER_STATE_REJECTED)
         * }
         * 
         * function returnHeldFunds (transfer, transaction) {
         *    return Promise.all(transfer.debits.map((debit) => {
         *      return Promise.all([
         *        _adjustBalance('hold', -debit.amount, transaction),
         *        _adjustBalance(debit.account, debit.amount, transaction),
         *        _insertEntryByName(debit.account, transfer.id, transaction)
         *      ])
         *    }))
         *  }
         */
        // FIXME TODO
        throw new RuntimeException("Not implemented");
    }

}
