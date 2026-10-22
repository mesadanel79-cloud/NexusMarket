package application.domain.services.authorization;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.valueobjects.UserRole;

/**
 * Resolves the commercial profile of the acting user from persistence and validates
 * that the user only acts on its own resources.
 */
public class ValidateOwnershipService {

    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ValidateOwnershipService(ValidateUserAuthorizationService validateUserAuthorizationService,
                                    BuyerRepositoryPort buyerRepositoryPort,
                                    SellerRepositoryPort sellerRepositoryPort) {
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    /** Requires a BUYER user and returns its authoritative buyer profile. */
    public Buyer requireBuyer(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.BUYER);
        if (user.getBuyer() == null) {
            throw new UnauthorizedOperationException("User is not linked to a buyer profile.");
        }
        return buyerRepositoryPort.findByIdentification(user.getBuyer())
                .orElseThrow(() -> new EntityNotFoundException("Buyer", user.getBuyer().getIdentification()));
    }

    /** Requires a SELLER user and returns its authoritative seller profile. */
    public Seller requireSeller(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.SELLER);
        if (user.getSeller() == null) {
            throw new UnauthorizedOperationException("User is not linked to a seller profile.");
        }
        return sellerRepositoryPort.findByIdentification(user.getSeller())
                .orElseThrow(() -> new EntityNotFoundException("Seller", user.getSeller().getIdentification()));
    }

    public void requireOrderOwnership(Buyer buyer, Order order) {
        if (!order.belongsTo(buyer)) {
            throw new UnauthorizedOperationException("Order " + order.getIdentifier() + " does not belong to the buyer.");
        }
    }

    public void requireProductOwnership(Seller seller, Product product) {
        if (!product.isOwnedBy(seller)) {
            throw new UnauthorizedOperationException("Product " + product.getIdentifier() + " does not belong to the seller.");
        }
    }
}
