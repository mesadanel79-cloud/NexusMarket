package application.domain.services.buyer;

import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.services.authorization.ValidateOwnershipService;

/**
 * Returns the profile of the authenticated buyer.
 */
public class ConsultBuyerProfileService {

    private final ValidateOwnershipService validateOwnershipService;

    public ConsultBuyerProfileService(ValidateOwnershipService validateOwnershipService) {
        this.validateOwnershipService = validateOwnershipService;
    }

    public Buyer consult(User user) {
        return validateOwnershipService.requireBuyer(user);
    }
}
