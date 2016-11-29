package org.interledger.ilp.core;

import org.interledger.ilp.core.ledger.model.LedgerInfo;

public interface Ledger {

    /**
     * Retrieve some meta-data about the ledger. (useful for Wallet/ILP-connector)
     *
     * @return <code>LedgerInfo</code>
     */
    LedgerInfo getInfo();

}
