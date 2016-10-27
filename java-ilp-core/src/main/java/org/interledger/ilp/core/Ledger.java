package org.interledger.ilp.core;

public interface Ledger {

    /**
     * Retrieve some meta-data about the ledger. (useful for Wallet/ILP-connector)
     *
     * @return <code>LedgerInfo</code>
     */
    LedgerInfo getInfo();

}
