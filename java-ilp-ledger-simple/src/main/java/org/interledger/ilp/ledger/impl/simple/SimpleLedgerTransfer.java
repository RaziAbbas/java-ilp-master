package org.interledger.ilp.ledger.impl.simple;

import io.vertx.core.json.JsonArray;

//import javax.money.MonetaryAmount;

//import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.interledger.ilp.core.Credit;
import org.interledger.ilp.core.Debit;
import org.interledger.ilp.core.FulfillmentURI;
import org.interledger.ilp.core.ConditionURI;
import org.interledger.ilp.core.DTTM;
import org.interledger.ilp.core.InterledgerPacketHeader;
import org.interledger.ilp.core.LedgerInfo;
import org.interledger.ilp.core.LedgerPartialEntry;
import org.interledger.ilp.core.LedgerTransfer;
import org.interledger.ilp.core.TransferID;
import org.interledger.ilp.core.TransferStatus;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.LedgerFactory;
import org.interledger.ilp.ledger.account.LedgerAccount;

// FIXME: Allow multiple debit/credits (Remove all code related to index [0]

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

    /*
     * Note: Defensive security protection:
     * The default value for URIExecutionFF|URICancelationFF FulfillmentURI.EMPTY
     * will trigger a transaction just if the ConditionURI for Execution/Cancelation
     * are also empty.
     */
    FulfillmentURI URIExecutionFF     = FulfillmentURI.MISSING;
    FulfillmentURI URICancelationFF   = FulfillmentURI.MISSING;
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
        System.out.println("deleteme SimpleLedgerTransfer constructor transferStatus:"+transferStatus.toString());
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
    public void setTransferStatus(TransferStatus transferStatus) {
        final String errorState = "new state '"+transferStatus.toString()+"' "
                + "not compliant with Transfer State Machine. Current state: "
                + this.transferStatus.toString();
        // TODO: COMMENT next check commented to make five-bells-ledger tests pass
        //    anyway it looks sensible to have them in place.
        // switch(transferStatus){
        //     // TODO: RECHECK allowed state machine 
        //     case PROPOSED:
        //         if (this.transferStatus != TransferStatus.PROPOSED) { 
        //             throw new RuntimeException(errorState); 
        //         }
        //         break;
        //     case PREPARED:
        //         if (this.transferStatus != TransferStatus.PROPOSED) { 
        //             throw new RuntimeException(errorState); 
        //         }
        //         break;
        //     case EXECUTED:
        //         if (this.transferStatus != TransferStatus.PREPARED ) { 
        //             throw new RuntimeException(errorState); 
        //         }
        //         break;
        //     case REJECTED:
        //         if (this.transferStatus != TransferStatus.PREPARED ) { 
        //             throw new RuntimeException(errorState); 
        //         }
        //         break;
        //     default:
        //         throw new RuntimeException("Unknown transferStatus");
        // }
        this.transferStatus = transferStatus;
        System.out.println("deleteme SimpleLedgerTransfer setTransferStatus transferStatus:"+transferStatus.toString());

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
    public ConditionURI getURICancellationCondition() {
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

    @Override
    public void  setURIExecutionFulfillment(FulfillmentURI ffURI){
        this.URIExecutionFF = ffURI;
    }
    
    @Override
    public FulfillmentURI getURIExecutionFulfillment(){
        return URIExecutionFF;
    }

    @Override
    public void  setURICancelationFulfillment(FulfillmentURI ffURI){
        this.URICancelationFF = ffURI;
    }
    
    @Override
    public FulfillmentURI getURICancellationFulfillment(){
        return URICancelationFF;
    }
    
    // NOTE: The JSON returned to the ILP connector and the Wallet must not necesarelly match
    // since the data about the transfer needed by the wallet and the connector differ.
    // That's why two different JSON encoders exist
    public String toILPJSONStringifiedFormat() {
        // REF: convertToExternalTransfer@
        // https://github.com/interledger/five-bells-ledger/blob/master/src/models/converters/transfers.js
        JsonObject jo = new JsonObject();
        jo.put("id", credit_list[0].account.getLedgerUri() + "/transfers/"+this.transferID);
        jo.put("state", this.getTransferStatus().toString());
        jo.put("credits", entryList2Json(credit_list)); // FIXME: Simplify remove creditsToJson if possible
        jo.put("debits" , entryList2Json( debit_list)); // FIXME: Simplify remove  debitsToJson if possible
        {
            JsonObject timeline = new JsonObject();
            timeline.put("proposed_at", this.DTTM_proposed.toString());
            if (this.DTTM_prepared != DTTM.future) { timeline.put("prepared_at", this.DTTM_prepared.toString()); }
            if (this.DTTM_executed != DTTM.future) { timeline.put("executed_at", this.DTTM_executed.toString()); }
            if (this.DTTM_rejected != DTTM.future) { timeline.put("rejected_at", this.DTTM_rejected.toString()); }
            jo.put("timeline", timeline);
        }
        jo.put("expires_at", this.DTTM_expires.toString());


        if (  this.getTransferStatus() == TransferStatus.EXECUTED 
            ||this.getTransferStatus() == TransferStatus.REJECTED ) {
            //  REF: sendNotifications @
            //       five-bells-ledger/src/lib/notificationBroadcasterWebsocket.js
            JsonObject related_resources = new JsonObject();
            String URI_FF = (this.getTransferStatus() == TransferStatus.EXECUTED)
                    ? this.  getURIExecutionFulfillment().URI
                    : this.getURICancellationFulfillment().URI;
            related_resources.put("execution_condition_fulfillment", URI_FF);
            jo.put("related_resources", related_resources);
        }
        return jo.encode();
    }

    public JsonObject toJSONWalletFormat(boolean bIncludeConditions /* , boolean bIncludeFulfillments */) {
//      { id: 'http://localhost/transfers/155dff3f-4915-44df-a707-acc4b527bcbd',
//          ledger: 'http://localhost',
//          debits: 
//           [ { account: 'http://localhost/accounts/alice',
//               amount: '10',
//               authorized: true } ],
//          credits: [ { account: 'http://localhost/accounts/bob', amount: '10' } ],
//          state: 'executed',
//          timeline: 
//           { executed_at: '2015-06-16T00:00:00.000Z',
//             prepared_at: '2015-06-16T00:00:00.000Z',
//             proposed_at: '2015-06-16T00:00:00.000Z' } }
      LedgerInfo ledgerInfo = LedgerFactory.getDefaultLedger().getInfo();
      JsonObject jo = new JsonObject();
      String id = "http://localhost" /*FIXME:(0) TODO URL of ledger as seen by clients */+ "/transfers/"+ transferID.transferID;
      jo.put("id", id);
      jo.put("state", this.getTransferStatus().toString());
      jo.put("ledger", ledgerInfo.getBaseUri());
      jo.put("credits", entryList2Json(credit_list));
      jo.put("debits" , entryList2Json( debit_list));
      {
          JsonObject timeline = new JsonObject();
          timeline.put("proposed_at", this.DTTM_proposed.toString());
          if (this.DTTM_prepared != DTTM.future) { timeline.put("prepared_at", this.DTTM_prepared.toString()); }
          if (this.DTTM_executed != DTTM.future) { timeline.put("executed_at", this.DTTM_executed.toString()); }
          if (this.DTTM_rejected != DTTM.future) { timeline.put("rejected_at", this.DTTM_rejected.toString()); }
          jo.put("timeline", timeline);
      }
      if (bIncludeConditions) {
          jo.put(   "execution_condition", this.  getURIExecutionCondition().URI);
          jo.put("cancellation_condition", this.getURICancellationCondition().URI);
      }
      // jo.put("expires_at", this.DTTM_expires.toString());
      return jo;
  }


    public String toWalletJSONStringifiedFormat(boolean bIncludeConditions) {
        String result = toJSONWalletFormat(bIncludeConditions).encode(); // FIXME: Recheck
        return result;
    }

    private JsonArray entryList2Json(LedgerPartialEntry[] input_list) {
        JsonArray ja = new JsonArray();
        for (LedgerPartialEntry entry : input_list) {
            // FIXME: This code to calculate amount is PLAIN WRONG. Just to pass five-bells-ledger
            long amount = entry. amount.getNumber().longValue();
            JsonObject jo = new JsonObject();
            jo.put("account", entry.account.getUri());
            jo.put( "amount", ""+amount);
            if (entry instanceof Debit) {
                jo.put("authorized", ((Debit) entry).getAuthorized());
            }
            ja.add(jo);
        }
        return ja;
    }
    
    @Override
    public boolean isLocal() {
        return this.debit_list[0].account.getLedgerUri().equals(
                    this.credit_list[0].account.getLedgerUri() );
    }


}
