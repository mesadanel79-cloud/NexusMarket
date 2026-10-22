package application.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.exceptions.UserAlreadyExistsException;
import application.domain.models.AdministrativeReport;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseType;
import application.support.ServiceHarness;
import application.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Responsibility matrix and general restrictions (RG-01..RG-03) of the specification.
 */
class AuthorizationRulesServiceTest {

    private ServiceHarness h;
    private TestFixtures f;

    @BeforeEach
    void setUp() {
        h = new ServiceHarness();
        f = new TestFixtures(h).scenario();
    }

    @Test
    void onlyAdminRegistersSellers() {
        User newSeller = TestFixtures.newSellerUser("seller.x", "900999999", "x@mail.com");
        assertThrows(UnauthorizedOperationException.class, () -> h.registerSeller.register(f.supervisor, newSeller,
                TestFixtures.warehouse("WH-X-01", WarehouseType.SELLER)));
        assertThrows(UnauthorizedOperationException.class, () -> h.registerSeller.register(f.buyer, newSeller,
                TestFixtures.warehouse("WH-X-01", WarehouseType.SELLER)));
    }

    @Test
    void sellerRegistrationCreatesItsFirstWarehouse() {
        assertEquals(WarehouseType.SELLER, h.warehouses.store.get("WH-SEL-01").getWarehouseType());
        assertEquals("900100100", h.warehouses.store.get("WH-SEL-01").getOwner().getIdentification());
    }

    @Test
    void identificationAndEmailAreUnique() {
        assertThrows(UserAlreadyExistsException.class,
                () -> h.registerBuyer.register(TestFixtures.newBuyerUser("buyer.dup", "1017000001", "new@mail.com")));
        assertThrows(UserAlreadyExistsException.class,
                () -> h.registerBuyer.register(TestFixtures.newBuyerUser("buyer.dup2", "999", "buyer@test.com")));
    }

    @Test
    void supervisorIsReadOnly() {
        assertThrows(UnauthorizedOperationException.class,
                () -> h.adjustInventory.adjust(f.supervisor, null, 1, "count"));
        AdministrativeReport report = h.report.generate(f.supervisor);
        assertEquals(1L, report.getUsersByRole().get("BUYER").longValue());
        assertNotNull(h.consultOperations.consultOperations(f.supervisor));
    }

    @Test
    void sellerCannotStockAnotherSellersProduct() {
        assertThrows(UnauthorizedOperationException.class, () -> h.inflow.registerInflow(f.otherSeller,
                TestFixtures.warehouseRef("WH-OTH-01"), TestFixtures.skuRef("LAP-8GB"), 1));
    }

    @Test
    void sellerCannotChangeAnotherSellersProduct() {
        assertThrows(UnauthorizedOperationException.class,
                () -> h.changeProductStatus.suspend(f.otherSeller, f.laptop));
    }

    @Test
    void blockedUsersCannotLoginAndOldTokensAreInvalidated() {
        User credentials = new User();
        credentials.setUsername("buyer.test");
        credentials.setPassword(TestFixtures.PASSWORD);
        AuthenticationResult result = h.login.login(credentials);
        assertEquals(1, result.getAuthenticatedUser().getAuthTokenVersion().intValue());

        h.changeUserStatus.changeStatus(f.admin, f.buyer, UserStatus.BLOCKED);
        assertEquals(2, h.users.store.get(f.buyer.getUserId()).getAuthTokenVersion().intValue());
        assertThrows(UnauthorizedOperationException.class, () -> h.login.login(credentials));

        credentials.setPassword("wrong-password");
        assertThrows(InvalidCredentialsException.class, () -> h.login.login(credentials));
    }

    @Test
    void eachUserHasASingleRole() {
        User user = TestFixtures.newStaff("dual.role", UserRole.SUPERVISOR, "S-77");
        user.setBuyer(TestFixtures.newBuyerUser("x", "x1", "x1@mail.com").getBuyer());
        assertThrows(application.domain.exceptions.InvalidUserException.class, user::activateOnRegistration);
    }
}
