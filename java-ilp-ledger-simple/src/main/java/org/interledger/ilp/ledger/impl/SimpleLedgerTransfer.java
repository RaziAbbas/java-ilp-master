package org.interledger.ilp.ledger.impl;

import javax.money.MonetaryAmount;

import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.ConditionURI;
import org.interledger.ilp.core.DTTM;
import org.interledger.ilp.core.InterledgerPacketHeader;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.core.TransferStatus;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.account.LedgerAccount;

public class SimpleLedgerTransfer implements LedgerTransfer {
    final InterledgerPacketHeader ph = null; // FIXME. Really needed?
    // TODO:(0) Use URI instead of string
    final TransferID transferID;
    final LedgerAccount fromAccount;
    final AccountUri fromAccountURI;
    final AccountUri toAccountURI;
    final MonetaryAmount ammount;
    // URI encoded execution & cancelation crypto-conditions
    final ConditionURI URIExecutionCond;
    final ConditionURI URICancelationCond;
    final DTTM DTTM_expires ;
    final DTTM DTTM_proposed;

    String data = "";
    String noteToSelf = "";
    
    TransferStatus transferStatus;
    DTTM DTTM_prepared = DTTM.future;
    DTTM DTTM_executed = DTTM.future;
    DTTM DTTM_rejected = DTTM.future;

    public SimpleLedgerTransfer(TransferID transferID, AccountUri fromAccount, AccountUri toAccount, 
        MonetaryAmount ammount, ConditionURI URIExecutionCond, 
        ConditionURI URICancelationCond, DTTM DTTM_expires, DTTM DTTM_proposed,
        String data, String noteToSelf, TransferStatus transferStatus ){
        if (fromAccount.ledger.equals(toAccount.ledger)) {
            throw new RuntimeException("assert exception: "
                    + "SimpleLedgerTransfer does not handle local transfers. Only transfers "
                    + "from local ledger to external ones");
        }
        // FIXME: TODO: If fromAccount.ledger != "our ledger" throw RuntimeException.
        this.transferID         = transferID        ;
        this.fromAccountURI     = fromAccount       ;
        this.toAccountURI       = toAccount         ;
        this.ammount            = ammount           ;
        this.data               = data              ;
        this.noteToSelf         = noteToSelf        ;
        this.URIExecutionCond   = URIExecutionCond  ;
        this.URICancelationCond = URICancelationCond;
        this.DTTM_expires       = DTTM_expires      ;
        this.DTTM_proposed      = DTTM_proposed     ;
        this.transferStatus     = transferStatus    ;

        /*
         *  Parse AccountUri to fetch local account
         *  AccountUri will be similar to http://localLedger/account/"accountId" ->
         *  we need the "accountId" to fetch the correct local "from" Account
         */

        this.fromAccount = LedgerAccountManagerFactory.
                getLedgerAccountManagerSingleton().
                    getAccountByName(fromAccountURI);
    }

    @Override
    public InterledgerPacketHeader getHeader() {
        return ph;
    }

    @Override
    public TransferID getTransferID() {
        return transferID;
    }

    @Override
    public AccountUri getFromAccount() {
        return fromAccountURI;
    }
    
    @Override
    public AccountUri getToAccount() {
        return toAccountURI;
    }

    @Override
    public MonetaryAmount getAmount() {
        return ammount;
    }
    
    @Override
    public String getData() {
        return data;
    }

    @Override
    public String getNoteToSelf() {
        return noteToSelf;
    }

    @Override
    public TransferStatus getTransferStatus() {
        return transferStatus;
    }

    
    @Override
    public ConditionURI getURIExecutionCondition() {
        return URIExecutionCond;
    }

    @Override
    public ConditionURI getURICancelationCondition() {
        return URICancelationCond;
    }

    @Override
    public DTTM getDTTM_prepared() {
        return DTTM_prepared;
    }

    @Override
    public void setDTTM_prepared(DTTM DTTM) {
        DTTM_prepared = DTTM;
    }

    @Override
    public DTTM getDTTM_executed() {
        return DTTM_executed;
    }

    @Override
    public void setDTTM_executed(DTTM DTTM) {
        DTTM_executed = DTTM;
    }

    @Override
    public DTTM getDTTM_rejected() {
        return DTTM_rejected;
    }

    @Override
    public void setDTTM_rejected(DTTM DTTM) {
        DTTM_rejected = DTTM;
    }

    @Override
    public DTTM getDTTM_expires() {
        return DTTM_expires;
    }

    @Override
    public DTTM getDTTM_proposed() {
        return DTTM_proposed;
    }


}
