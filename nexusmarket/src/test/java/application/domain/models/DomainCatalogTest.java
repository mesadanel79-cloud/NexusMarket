package application.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import application.domain.exceptions.InvalidCatalogValueException;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

class DomainCatalogTest {

    @Test
    void resolvesCodesCaseInsensitively() {
        assertEquals(UserRole.LOGISTIC_OPERATOR, UserRole.fromCode("logistic_operator"));
        assertEquals(OrderStatus.PENDING_PAYMENT, OrderStatus.fromCode("PENDING_PAYMENT"));
        assertNull(OrderStatus.fromCode(null));
    }

    @Test
    void rejectsUnknownCodes() {
        assertThrows(InvalidCatalogValueException.class, () -> UserRole.fromCode("CASHIER"));
    }

    @Test
    void valuesOfDifferentCatalogsAreNeverEqual() {
        assertFalse(UserStatus.ACTIVE.equals(BuyerStatus.ACTIVE));
    }

    @Test
    void catalogsMatchTheFunctionalSpecification() {
        assertEquals(5, UserRole.values().size());
        assertEquals(2, UserStatus.values().size());
        assertEquals(6, MovementType.values().size());
    }
}
