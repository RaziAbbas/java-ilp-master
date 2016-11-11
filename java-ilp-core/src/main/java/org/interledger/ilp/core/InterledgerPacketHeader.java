package org.interledger.ilp.core;

/**
 * Immutable Interledger Packet Header
 *
 */
public class InterledgerPacketHeader {

    public InterledgerPacketHeader(String /* FIXME:Improvement Replace String with ILP related type */ destinationAddress , String amount,
            ConditionURI condition, DTTM expiry) {
        this.destinationAddress = destinationAddress;
        this.amount = amount;
        this.condition = condition;
        this.expiry = expiry;

        if (condition == null && expiry != null) {
            throw new IllegalArgumentException("Must provide a condition if providing an expiry.");
        }

        if (condition != null && expiry == null) {
            throw new IllegalArgumentException("Must provide an expiry if providing a condition.");
        }

        //TODO Validate address
        //TODO Validate amount
    }

    private String destinationAddress;
    private String amount;
    private ConditionURI condition;
    private DTTM expiry;

    /**
     * The ILP Address of the destination account
     *
     * @return the destinationAddress
     */
    public String getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * The amount that must be transferred into the destination account
     *
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * The condition that must be fulfilled to release prepared transfers
     *
     * @return the condition
     */
    public ConditionURI getCondition() {
        return condition;
    }

    /**
     * The expiry of the payment after which any prepared transfers must be
     * rolled back.
     *
     * @return the expiry
     */
    public DTTM getExpiry() {
        return expiry;
    }

    /**
     * Checks if this header is for an optimistic mode payment.
     *
     * @return {@code true} if the header contains no timeout and condition
     */
    public boolean isOptimisticModeHeader() {
        return (this.condition == null && this.expiry == null);
    }

}
