package org.interledger.ilp.core;

public interface LedgerTransfer {

    /**
     * Get the packet header for this transfer
     *
     * @return the Interledger Packet Header
     */
    InterledgerPacketHeader getHeader(); // FIXME: Really needed?
    
    /*
     * Get the transfer Unique ID
     */
    TransferID getTransferID();

    /**
     * Get the local account that funds are being debited from.
     *
     * @return local account identifier
     */
    Credit[] getCredits();

    /**
     * Get the local account that funds are being debited from.
     *
     * @return local account identifier
     */
    Debit[] getDebits();

    /**
     * Get the data to be sent.
     *
     * Ledger plugins SHOULD treat this data as opaque, however it will usually
     * start with an ILP header followed by a transport layer header, a quote
     * request or a custom user-provided data packet.
     *
     * If the data is too large, the ledger plugin MUST throw a
     * MaximumDataSizeExceededError. If the data is too large only because the
     * amount is insufficient, the ledger plugin MUST throw an
     * InsufficientAmountError.
     *
     * @return a buffer containing the data
     */
    String getData();

    /**
     * Get the host's internal memo
     *
     * An optional bytestring containing details the host needs to persist with
     * the transfer in order to be able to react to transfer events like
     * condition fulfillment later.
     *
     * Ledger plugins MAY attach the noteToSelf to the transfer and let the
     * ledger store it. Otherwise it MAY use the store in order to persist this
     * field. Regardless of the implementation, the ledger plugin MUST ensure
     * that all instances of the transfer carry the same noteToSelf, even across
     * different machines.
     *
     * Ledger plugins MUST ensure that the data in the noteToSelf either isn't
     * shared with any untrusted party or encrypted before it is shared.
     *
     * @return a buffer containing the data
     */
    String getNoteToSelf();
    
    void setTransferStatus(TransferStatus transferStatus);
    TransferStatus getTransferStatus();

    public DTTM getDTTM_prepared();
    public void setDTTM_prepared(DTTM dTTM_prepared);

    public DTTM getDTTM_executed();
    public void setDTTM_executed(DTTM dTTM_executed);

    public DTTM getDTTM_rejected();
    public void setDTTM_rejected(DTTM dTTM_rejected);

    public DTTM getDTTM_expires();

    public DTTM getDTTM_proposed();

    public ConditionURI getURIExecutionCondition();

    public ConditionURI getURICancelationCondition();

    public void           setURIExecutionFulfillment(FulfillmentURI ffURI);
    public FulfillmentURI getURIExecutionFulfillment();

    public void           setURICancelationFulfillment(FulfillmentURI ffURI);
    public FulfillmentURI getURICancelationFulfillment();

}
