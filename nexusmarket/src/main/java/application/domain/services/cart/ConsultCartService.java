package application.domain.services.cart;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;

/**
 * Returns the active cart of the authenticated buyer (an empty cart if none exists).
 */
public class ConsultCartService {

    private final CartRepositoryPort cartRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;

    public ConsultCartService(CartRepositoryPort cartRepositoryPort, ValidateOwnershipService validateOwnershipService) {
        this.cartRepositoryPort = cartRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
    }

    public Cart consult(User user) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        return loadOrOpen(buyer);
    }

    Cart loadOrOpen(Buyer buyer) {
        return cartRepositoryPort.findByBuyer(buyer).orElseGet(() -> Cart.openFor(buyer));
    }
}
