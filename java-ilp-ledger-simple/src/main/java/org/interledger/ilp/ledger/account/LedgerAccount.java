package org.interledger.ilp.ledger.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.money.MonetaryAmount;

import org.interledger.ilp.core.AccountURI;

/**
 * This interface defines a ledger account.
 *
 * @author mrmx
 */
public interface LedgerAccount {

    AccountURI getName();

    String getCurrencyCode();

    LedgerAccount setBalance(Number balance);

    LedgerAccount setBalance(MonetaryAmount balance);

    MonetaryAmount getBalance();

    @JsonProperty("balance")
    String getBalanceAsString();

    Number getBalanceAsNumber();

    // FIXME: credit & debit not needed must be associated 
    //  to transactions, not Accounts. Accounts must keep only
    // the balance.
    LedgerAccount credit(Number amount);

    LedgerAccount credit(MonetaryAmount amount);

    LedgerAccount debit(Number amount);

    LedgerAccount debit(MonetaryAmount amount);
}
