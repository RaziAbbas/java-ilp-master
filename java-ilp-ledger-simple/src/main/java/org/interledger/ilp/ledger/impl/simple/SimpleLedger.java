package org.interledger.ilp.ledger.impl.simple;

import org.interledger.cryptoconditions.Fulfillment;
import org.interledger.ilp.core.Ledger;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.LedgerTransferRejectedReason;
import org.interledger.ilp.core.events.LedgerEventHandler;
import org.interledger.ilp.common.config.Config;

/**
 * Simple in-memory ledger implementation
 *
 * @author mrmx
 */
public class SimpleLedger implements Ledger {

    private LedgerInfo info;
    private String name;
    private Config config;

    public SimpleLedger(LedgerInfo info, String name, Config config) {
        this.info = info;
        this.name = name;
        this.config = config;
    }

    public LedgerInfo getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    /**
     * @return internal implementation server config parameters (public/private keys, ...)
     */
    public Config getConfig() {
        return config;
    }

    @Override
    public void send(LedgerTransfer transfer) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void rejectTransfer(LedgerTransfer transfer, LedgerTransferRejectedReason reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void fulfillCondition(Fulfillment fulfillment) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void registerEventHandler(LedgerEventHandler<?> handler) {
        // TODO Auto-generated method stub
        
    }

//    public void send(LedgerTransfer transfer) {
//        LedgerAccountManager accountManager = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton();
//        LedgerAccount debitFrom = accountManager.getAccountByName(transfer. getDebits()[0].account.getAccountId());
//        LedgerAccount creditTo  = accountManager.getAccountByName(transfer.getCredits()[0].account.getAccountId());
//        if (creditTo.equals(debitFrom)) {
//            throw new RuntimeException("accounts are the same");
//        }
//        MonetaryAmount amount = transfer.getCredits()[0].amount;
//        if (debitFrom.getBalance().isGreaterThanOrEqualTo(amount)) {
//            debitFrom.debit(amount);
//            creditTo.credit(amount);
//        } else {
//            throw new InterledgerException(InterledgerException.RegisteredException.InsufficientAmountError, amount.toString());
//        }
//    }

//    public void rejectTransfer(LedgerTransfer transfer, LedgerTransferRejectedReason reason) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    public void fulfillCondition(Fulfillment fulfillment) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void registerEventHandler(LedgerEventHandler<?> handler) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
}
