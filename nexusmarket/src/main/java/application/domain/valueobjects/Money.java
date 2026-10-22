package application.domain.valueobjects;

import application.domain.exceptions.CurrencyMismatchException;
import application.domain.exceptions.InvalidMoneyException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable Value Object representing a non-negative amount of money in a currency.
 *
 * <p>Invariants: amount and currency are mandatory, the amount is never negative and
 * it is always normalized to two decimals. Arithmetic between different currencies
 * is rejected.
 */
public final class Money {

    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new InvalidMoneyException("Money amount must be provided.");
        }
        if (currency == null) {
            throw new InvalidMoneyException("Money currency must be provided.");
        }
        if (amount.signum() < 0) {
            throw new InvalidMoneyException("Money amount must not be negative.");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean hasSameCurrency(Money other) {
        return other != null && currency.equals(other.currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        BigDecimal result = amount.subtract(other.amount);
        if (result.signum() < 0) {
            throw new InvalidMoneyException("Money subtraction would produce a negative amount.");
        }
        return new Money(result, currency);
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new InvalidMoneyException("Money cannot be multiplied by a negative factor.");
        }
        return new Money(amount.multiply(BigDecimal.valueOf(factor)), currency);
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    private void requireSameCurrency(Money other) {
        if (other == null) {
            throw new InvalidMoneyException("Money operand must be provided.");
        }
        if (!hasSameCurrency(other)) {
            throw new CurrencyMismatchException("Currency mismatch: " + currency.getCode()
                    + " vs " + other.currency.getCode() + ".");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Money money)) {
            return false;
        }
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency.getCode();
    }
}
