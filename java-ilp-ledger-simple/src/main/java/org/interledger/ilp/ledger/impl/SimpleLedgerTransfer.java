package org.interledger.ilp.ledger.impl;

import javax.money.MonetaryAmount;

import org.interledger.ilp.core.AccountURI;
import org.interledger.ilp.core.ConditionURI;
import org.interledger.ilp.core.DTTM;
import org.interledger.ilp.core.InterledgerPacketHeader;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferStatus;

public class SimpleLedgerTransfer implements LedgerTransfer {
    final InterledgerPacketHeader ph = null; // FIXME. Really needed?
    // TODO:(0) Use URI instead of string
    final AccountURI fromAccount;
    final AccountURI toAccount;
    final MonetaryAmount ammount;
    final ConditionURI URIExecutionCondition;
    final ConditionURI URICancelationCondition;
    final DTTM DTTM_expires ;
    final DTTM DTTM_proposed;

    String data;
    String noteToSelf;
    
    TransferStatus transferStatus;
    DTTM DTTM_prepared;
    DTTM DTTM_executed;
    DTTM DTTM_rejected;

    SimpleLedgerTransfer(AccountURI fromAccount, AccountURI toAccount, 
        MonetaryAmount ammount, ConditionURI URIExecutionCondition, ConditionURI URICancelationCondition,
        DTTM DTTM_expires, DTTM DTTM_proposed,
        String data, String noteToSelf, TransferStatus transferStatus ){
        this.fromAccount = fromAccount;
        this.toAccount   = toAccount  ;
        this.ammount     = ammount    ;
        this.data        = data       ;
        this.noteToSelf  = noteToSelf ;
        this.URIExecutionCondition = URIExecutionCondition;
        this.URICancelationCondition = URICancelationCondition;
        this.DTTM_expires = DTTM_expires;
        this.DTTM_proposed = DTTM_proposed;
    }

    @Override
    public InterledgerPacketHeader getHeader() {
        return ph;
    }

    @Override
    public AccountURI getFromAccount() {
        return fromAccount;
    }
    
    @Override
    public AccountURI getToAccount() {
        return toAccount;
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
        return URIExecutionCondition;
    }

    @Override
    public ConditionURI getURICancelationCondition() {
        return URICancelationCondition;
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
