package org.interledger.ilp.core;


public enum TransferStatus {
    NONEXISTENT(-1), // transferStates.TRANSFER_STATE_NONEXISTENT
    PROPOSED(0),
    PREPARED(1),
    EXECUTED(2),
    REJECTED(3);

    private final int statusCode;

    TransferStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
		return statusCode;
	}

    public static TransferStatus valueOf(int statusCode) {
        switch (statusCode) {
            case -1:
                throw new RuntimeException("NONEXISTENT TransferStatus must never be used except for "
                        + "object equality comparation" );
            case 0:
                return TransferStatus.PROPOSED;
            case 1:
                return TransferStatus.PREPARED;
            case 2:
                return TransferStatus.EXECUTED;
            case 3:
                return TransferStatus.REJECTED;
            default:
                throw new IllegalArgumentException("Invalid status " + statusCode);
        }
    }
    
    @Override
    public String toString() {
        switch (statusCode) {
        case -1:
            return "nonexistent";
        case 0:
            return "proposed";
        case 1:
            return "prepared";
        case 2:
            return "executed";
        case 3:
            return "rejected";
        default:
            throw new IllegalArgumentException("Invalid status " + statusCode);
    }
    }
}