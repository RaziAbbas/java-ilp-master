package org.interledger.ilp.ledger.impl.simple;

import java.util.Currency;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import org.interledger.ilp.core.AccountUri;
import org.interledger.ilp.ledger.LedgerAccountManagerFactory;
import org.interledger.ilp.ledger.MoneyUtils;
import org.interledger.ilp.ledger.account.LedgerAccount;
import org.javamoney.moneta.Money;

/**
 * Represents a simple ledger account.
 *
 * @author mrmx
 */
public class SimpleLedgerAccount implements LedgerAccount {

    private AccountUri accountUri;
    private final String name;
    private final String currencyCode;
    private MonetaryAmount balance;
    private MonetaryAmount minimumAllowedBalance;
    private Boolean admin;
    private boolean disabled;
    private String connector;

    public SimpleLedgerAccount(String name, Currency currency) {
        this(name, currency.getCurrencyCode());
    }

    public SimpleLedgerAccount(String name, CurrencyUnit currencyUnit) {
        this(name, currencyUnit.getCurrencyCode());
    }

    public SimpleLedgerAccount(String name, String currencyCode) {
        this.name = name;
        this.currencyCode = currencyCode;
        this.balance = Money.of(0, currencyCode);
        this.minimumAllowedBalance = Money.of(0, currencyCode);
        this.disabled = false;
    }

    @Override
    public String getId() {
        return getAccountUri().getUri();
    }

    @Override
    public String getName() {
        return name;
    }

    public LedgerAccount setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

    @Override
    public Boolean isAdmin() {
        return admin;
    }

    public LedgerAccount setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public LedgerAccount setMinimumAllowedBalance(Number balance) {
        return setMinimumAllowedBalance(Money.of(balance, currencyCode));
    }

    @Override
    public LedgerAccount setMinimumAllowedBalance(MonetaryAmount balance) {
        this.minimumAllowedBalance = balance;
        return this;
    }

    @Override
    public MonetaryAmount getMinimumAllowedBalance() {
        return minimumAllowedBalance;
    }

    @Override
    public String getMinimumAllowedBalanceAsString() {
        return getMinimumAllowedBalance().getNumber().toString();
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
        return getBalance().getNumber().toString();
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }

    @Override
    public String getConnector() {
        return connector;
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
        if (obj == null || !(obj instanceof SimpleLedgerAccount)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return getId().equalsIgnoreCase(((SimpleLedgerAccount) obj).getId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("[");
        sb.append("name:").append(getName()).append(",");
        sb.append("balance:").append(getBalance());
        sb.append("]");
        return sb.toString();

    }

    protected MonetaryAmount toMonetaryAmount(String amount) {
        return MoneyUtils.toMonetaryAmount(amount, currencyCode);
    }

    private AccountUri getAccountUri() {
        if (accountUri == null) {
            accountUri = LedgerAccountManagerFactory.getLedgerAccountManagerSingleton().getAccountUri(this);
        }
        return accountUri;
    }
}
