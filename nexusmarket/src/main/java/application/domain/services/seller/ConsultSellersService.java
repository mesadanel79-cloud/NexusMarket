package application.domain.services.seller;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Consultation of sellers: the seller's own store profile, or the full list for staff.
 */
public class ConsultSellersService {

    private final SellerRepositoryPort sellerRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultSellersService(SellerRepositoryPort sellerRepositoryPort,
                                 ValidateOwnershipService validateOwnershipService,
                                 ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    public Seller consultMyStore(User user) {
        return validateOwnershipService.requireSeller(user);
    }

    public List<Seller> consultAll(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.ADMIN, UserRole.SUPERVISOR);
        return sellerRepositoryPort.findAll();
    }
}
