package application.domain.services.returns;

import application.domain.models.Buyer;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.ReturnRequestRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Return queries: the buyer's own returns and the staff lists by status.
 */
public class ConsultReturnsService {

    private final ReturnRequestRepositoryPort returnRequestRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultReturnsService(ReturnRequestRepositoryPort returnRequestRepositoryPort,
                                 ValidateOwnershipService validateOwnershipService,
                                 ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.returnRequestRepositoryPort = returnRequestRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    public List<ReturnRequest> consultBuyerReturns(User user) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        return returnRequestRepositoryPort.findByBuyer(buyer);
    }

    public List<ReturnRequest> consultByStatus(User user, ReturnStatus status) {
        validateUserAuthorizationService.requireRole(user, UserRole.ADMIN, UserRole.SUPERVISOR, UserRole.LOGISTIC_OPERATOR);
        return returnRequestRepositoryPort.findByStatus(status);
    }
}
