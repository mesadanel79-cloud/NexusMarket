package application.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.domain.exceptions.CurrencyMismatchException;
import application.domain.exceptions.InvalidMoneyException;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void normalizesToTwoDecimalsAndAdds() {
        Money total = Money.of(new BigDecimal("10.005"), Currency.COP).add(Money.of(BigDecimal.ONE, Currency.COP));
        assertEquals(new BigDecimal("11.01"), total.getAmount());
    }

    @Test
    void rejectsNegativeAmounts() {
        assertThrows(InvalidMoneyException.class, () -> Money.of(new BigDecimal("-1"), Currency.COP));
        assertThrows(InvalidMoneyException.class,
                () -> Money.of(BigDecimal.ONE, Currency.COP).subtract(Money.of(BigDecimal.TEN, Currency.COP)));
    }

    @Test
    void rejectsMixedCurrencies() {
        assertThrows(CurrencyMismatchException.class,
                () -> Money.of(BigDecimal.ONE, Currency.COP).add(Money.of(BigDecimal.ONE, Currency.USD)));
    }

    @Test
    void equalityIgnoresScale() {
        assertTrue(Money.of(new BigDecimal("5"), Currency.USD).equals(Money.of(new BigDecimal("5.00"), Currency.USD)));
    }
}
