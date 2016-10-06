package org.interledger.ilp.ledger.impl;

import java.util.Currency;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.ledger.MoneyUtils;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.javamoney.moneta.Money;

/**
 * Represents a simple ledger account.
 *
 * @author mrmx
 */
public class SimpleLedgerAccount implements LedgerAccount {

    private final AccountUri accountUri;
    private final String name;
    private final String currencyCode;
    private MonetaryAmount balance;

    public SimpleLedgerAccount(AccountUri accountUri, Currency currency) {
        this(accountUri, currency.getCurrencyCode());
    }

    public SimpleLedgerAccount(AccountUri accountUri, CurrencyUnit currencyUnit) {
        this(accountUri, currencyUnit.getCurrencyCode());
    }

    public SimpleLedgerAccount(AccountUri accountUri, String currencyCode) {
        this.accountUri = accountUri;
        this.currencyCode = currencyCode;
        this.name = accountUri.accoundId;
    }    
    
    @Override
    public AccountUri getAccountUri() {
        return accountUri;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public SimpleLedgerAccount setBalance(Number balance) {
        return setBalance(Money.of(balance, currencyCode));
    }

    @Override
    public SimpleLedgerAccount setBalance(MonetaryAmount balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public MonetaryAmount getBalance() {
        if (balance == null) {
            balance = Money.of(0, currencyCode);
        }
        return balance;
    }
    
    @Override
    public String getBalanceAsString() {
        return getBalanceAsNumber().toString();
    }

    @Override
    public Number getBalanceAsNumber() {
        return getBalance().getNumber();
    }
    
    public SimpleLedgerAccount credit(String amount) {
        return credit(toMonetaryAmount(amount));
    }

    @Override
    public SimpleLedgerAccount credit(Number amount) {
        return credit(Money.of(amount, currencyCode));
    }

    @Override
    public SimpleLedgerAccount credit(MonetaryAmount amount) {
        setBalance(getBalance().add(amount));
        return this;
    }

    public SimpleLedgerAccount debit(String amount) {
        return debit(toMonetaryAmount(amount));
    }
    
    @Override
    public SimpleLedgerAccount debit(Number amount) {
        return debit(Money.of(amount, currencyCode));
    }

    @Override
    public SimpleLedgerAccount debit(MonetaryAmount amount) {
        setBalance(getBalance().subtract(amount));
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof SimpleLedgerAccount)) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        return accountUri.URI.equalsIgnoreCase(((SimpleLedgerAccount)obj).getAccountUri().URI);
    }
    
    @Override
    public String toString() {
        return "Account["
                + "name:" + accountUri
                + " balance:" + balance
                + "]";
    }

    protected MonetaryAmount toMonetaryAmount(String amount) {
        return MoneyUtils.toMonetaryAmount(amount, currencyCode);
    }

}
