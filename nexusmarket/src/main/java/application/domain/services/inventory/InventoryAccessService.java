package application.domain.services.inventory;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.UserRole;

/**
 * Inventory administration is shared by the LOGISTIC_OPERATOR (any product) and the
 * SELLER (only its own products), as defined by the responsibility matrix.
 */
public class InventoryAccessService {

    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final ValidateOwnershipService validateOwnershipService;

    public InventoryAccessService(ValidateUserAuthorizationService validateUserAuthorizationService,
                                  ValidateOwnershipService validateOwnershipService) {
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.validateOwnershipService = validateOwnershipService;
    }

    /** Role check only; call it before loading any inventory data. */
    public void requireInventoryRole(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.LOGISTIC_OPERATOR, UserRole.SELLER);
    }

    public void requireInventoryAdministrator(User user, Product product) {
        requireInventoryRole(user);
        if (UserRole.SELLER.equals(user.getRole())) {
            Seller seller = validateOwnershipService.requireSeller(user);
            if (product == null || !product.isOwnedBy(seller)) {
                throw new UnauthorizedOperationException("Sellers can only manage the inventory of their own products.");
            }
        }
    }
}
