package application.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.domain.exceptions.InvalidPersonDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.InvalidUserException;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import application.support.TestFixtures;
import org.junit.jupiter.api.Test;

class UserAndBuyerTest {

    @Test
    void userRegistrationActivatesAndBlockingBumpsTokenVersion() {
        User user = TestFixtures.newStaff("operator1", UserRole.LOGISTIC_OPERATOR, "L-9");
        user.activateOnRegistration();
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        user.changeStatus(UserStatus.BLOCKED);
        assertEquals(2, user.getAuthTokenVersion().intValue());
        assertThrows(InvalidStatusTransitionException.class, () -> user.changeStatus(UserStatus.BLOCKED));
    }

    @Test
    void buyerRoleRequiresBuyerProfile() {
        User user = TestFixtures.newStaff("buyer1", UserRole.BUYER, "B-9");
        assertThrows(InvalidUserException.class, user::activateOnRegistration);
    }

    @Test
    void contactDataIsValidated() {
        User user = TestFixtures.newStaff("staff2", UserRole.ADMIN, "A-9");
        user.setEmail("not-an-email");
        assertThrows(InvalidPersonDataException.class, user::activateOnRegistration);
        user.setEmail("ok@mail.com");
        user.setPhoneNumber("123");
        assertThrows(InvalidPersonDataException.class, user::activateOnRegistration);
    }

    @Test
    void buyerAcceptsMainOrAdditionalAddressesOnly() {
        Buyer buyer = TestFixtures.newBuyerUser("buyer2", "111", "b2@mail.com").getBuyer();
        buyer.register();
        assertTrue(buyer.isRegisteredAddress("main street 1"));
        assertTrue(buyer.isRegisteredAddress("Office street 2"));
        assertFalse(buyer.isRegisteredAddress("Unknown address"));
    }

    @Test
    void suspendedBuyerCannotPurchase() {
        Buyer buyer = TestFixtures.newBuyerUser("buyer3", "222", "b3@mail.com").getBuyer();
        buyer.register();
        buyer.changeStatus(BuyerStatus.SUSPENDED);
        assertFalse(buyer.canPurchase());
    }
}
