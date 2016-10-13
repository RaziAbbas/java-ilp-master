package org.interledger.ilp.ledger.impl.simple;

import javax.money.MonetaryAmount;
import org.interledger.cryptoconditions.Fulfillment;
import org.interledger.ilp.core.Ledger;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.LedgerTransferRejectedReason;
import org.interledger.ilp.core.events.LedgerEventHandler;
import org.interledger.ilp.core.exceptions.InsufficientAmountException;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.interledger.ilp.ledger.account.LedgerAccountManager;

/**
 * Simple in-memory ledger implementation
 *
 * @author mrmx
 */
public class SimpleLedger implements Ledger {

    private LedgerInfo info;
    private String name;

    public SimpleLedger(LedgerInfo info, String name) {
        this.info = info;
        this.name = name;
    }

    public LedgerInfo getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }


    public void send(LedgerTransfer transfer) {
        LedgerAccountManager accountManager = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton();
        LedgerAccount debitFrom = accountManager.getAccountByName(transfer. getDebits()[0].account.getAccountId());
        LedgerAccount creditTo  = accountManager.getAccountByName(transfer.getCredits()[0].account.getAccountId());
        if (creditTo.equals(debitFrom)) {
            throw new RuntimeException("accounts are the same");
        }
        MonetaryAmount amount = transfer.getCredits()[0].amount;
        if (debitFrom.getBalance().isGreaterThanOrEqualTo(amount)) {
            debitFrom.debit(amount);
            creditTo.credit(amount);
        } else {
            throw new InsufficientAmountException(amount.toString());
        }
    }

    public void rejectTransfer(LedgerTransfer transfer, LedgerTransferRejectedReason reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fulfillCondition(Fulfillment fulfillment) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void registerEventHandler(LedgerEventHandler<?> handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
