package application.adapters.persistence.jpa.mappers;

import application.domain.models.User;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.DomainCatalog;
import application.domain.valueobjects.Money;
import java.math.BigDecimal;

/**
 * Shared conversions between Value Objects and persistence columns.
 */
public final class JpaMapperSupport {

    private JpaMapperSupport() {
    }

    public static String code(DomainCatalog value) {
        return value == null ? null : value.getCode();
    }

    public static BigDecimal amount(Money money) {
        return money == null ? null : money.getAmount();
    }

    public static String currency(Money money) {
        return money == null ? null : money.getCurrency().getCode();
    }

    public static Money money(BigDecimal amount, String currency) {
        if (amount == null || currency == null) {
            return null;
        }
        return Money.of(amount, Currency.fromCode(currency));
    }

    public static Integer userId(User user) {
        return user == null ? null : user.getUserId();
    }

    /** Lightweight reference to a user stored only by id. */
    public static User userReference(Integer userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setUserId(userId);
        return user;
    }
}
