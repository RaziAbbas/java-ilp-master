package org.interledger.ilp.core;

public class TransferID {
    public final String transferID;
        
    public TransferID(String transferID) {
        if (transferID == null) throw new RuntimeException("transferID can't be null");
        // FIXME: Check format
        this.transferID = transferID;
    }
}
