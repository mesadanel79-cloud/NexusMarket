package application.domain.services.returns;

import application.domain.enums.ApprovalDecision;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidReturnException;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.ReturnRequestRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;

/**
 * An ADMIN approves or rejects a requested return.
 */
public class ReviewReturnService {

    private final ReturnRequestRepositoryPort returnRequestRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ReviewReturnService(ReturnRequestRepositoryPort returnRequestRepositoryPort,
                               ValidateUserAuthorizationService validateUserAuthorizationService,
                               RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.returnRequestRepositoryPort = returnRequestRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public ReturnRequest review(User user, ReturnRequest request, ApprovalDecision decision, String rejectionReason) {
        validateUserAuthorizationService.requireRole(user, UserRole.ADMIN);
        if (decision == null) {
            throw new InvalidReturnException("A review decision is required.");
        }
        ReturnRequest stored = returnRequestRepositoryPort.findByIdentifier(request)
                .orElseThrow(() -> new EntityNotFoundException("ReturnRequest", request.getIdentifier()));
        OperationType type;
        if (decision == ApprovalDecision.APPROVED) {
            stored.approve(user);
            type = OperationType.RETURN_APPROVAL;
        } else {
            stored.reject(user, rejectionReason);
            type = OperationType.RETURN_REJECTION;
        }
        returnRequestRepositoryPort.update(stored);
        registerOperationAndAuditService.register(type, user, stored,
                AuditDetails.of("decision", decision.name(), "rejectionReason", rejectionReason,
                        "newStatus", stored.getStatus().getCode()));
        return stored;
    }
}
