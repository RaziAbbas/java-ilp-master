package org.interledger.ilp.ledger.impl.simple;

import io.vertx.core.json.Json;

//import javax.money.MonetaryAmount;

//import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.core.Credit;
import org.interledger.ilp.core.Debit;

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
    final Credit[] credit_list;
    final Debit []  debit_list;
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

    public SimpleLedgerTransfer(TransferID transferID, 
        Debit[] debit_list, Credit[] credit_list, 
        ConditionURI URIExecutionCond, 
        ConditionURI URICancelationCond, DTTM DTTM_expires, DTTM DTTM_proposed,
        String data, String noteToSelf, TransferStatus transferStatus ){
        if (debit_list.length!=1) {
            throw new RuntimeException("Only one debit is supported in this implementation");
        }
        if (credit_list.length!=1) {
            throw new RuntimeException("Only one credit is supported in this implementation");
        }
        // TODO: FIXME: Check debit_list SUM of amounts equals credit_list SUM  of amounts.
        AccountUri fromAccount =  debit_list[0].account;
        AccountUri   toAccount = credit_list[0].account;

        if (fromAccount.getLedgerUri().equals(toAccount.getLedgerUri())) {
            throw new RuntimeException("assert exception: "
                    + "SimpleLedgerTransfer does not handle local transfers. Only transfers "
                    + "from local ledger to external ones");
        }
        // FIXME: TODO: If fromAccount.ledger != "our ledger" throw RuntimeException.
        this.transferID         = transferID        ;
        this.credit_list        = credit_list       ;
        this.debit_list         = debit_list        ;
        this.data               = data              ;
        this.noteToSelf         = noteToSelf        ;
        this.URIExecutionCond   = URIExecutionCond  ;
        this.URICancelationCond = URICancelationCond;
        this.DTTM_expires       = DTTM_expires      ;
        this.DTTM_proposed      = DTTM_proposed     ;
        this.transferStatus     = transferStatus    ;

        /*
         *  Parse String to fetch local accounturi
         *  String will be similar to http://localLedger/account/"accountId" ->
         *  we need the "accountId" to fetch the correct local "from" Account
         */

        this.fromAccount = LedgerAccountManagerFactory.
                getLedgerAccountManagerSingleton().
                    getAccountByName(credit_list[0].account.getAccountId());
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
    public Debit[] getDebits() {
        return debit_list;
    }

    @Override
    public Credit[] getCredits() {
        return credit_list;
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

    public String toILPJSONFormat() {
        // REF: convertToExternalTransfer@
        // https://github.com/interledger/five-bells-ledger/blob/master/src/models/converters/transfers.js
        JsonObject jo = new JsonObject();
        jo.put("id", credit_list[0].account.getLedgerUri() + "/transfers/"+this.transferID);
        jo.put("state", this.getTransferStatus());
        jo.put("credits", credit_list);
        jo.put("debits" ,  debit_list);
        {
            JsonObject timeline = new JsonObject();
            timeline.put("proposed_at", this.DTTM_proposed);
            if (this.DTTM_prepared != DTTM.future) { timeline.put("prepared_at", this.DTTM_prepared); }
            if (this.DTTM_executed != DTTM.future) { timeline.put("executed_at", this.DTTM_executed); }
            if (this.DTTM_rejected != DTTM.future) { timeline.put("rejected_at", this.DTTM_rejected); }
            jo.put("timeline", timeline);
        }
        jo.put("expires_at", this.DTTM_expires);
        return jo.encode();

        
    }
    
    public String toWalletJSONFormat() {
        return Json.encode(this); // FIXME: Recheck
    }

}
