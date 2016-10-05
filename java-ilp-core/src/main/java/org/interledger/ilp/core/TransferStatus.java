package org.interledger.ilp.core;


public enum TransferStatus {
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
}