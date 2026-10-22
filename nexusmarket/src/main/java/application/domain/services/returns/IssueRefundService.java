package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.ReturnRequestRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;

/**
 * An ADMIN issues the refund of a received return and registers it against the
 * order invoice.
 */
public class IssueRefundService {

    private final ReturnRequestRepositoryPort returnRequestRepositoryPort;
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public IssueRefundService(ReturnRequestRepositoryPort returnRequestRepositoryPort,
                              InvoiceRepositoryPort invoiceRepositoryPort,
                              ValidateUserAuthorizationService validateUserAuthorizationService,
                              RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.returnRequestRepositoryPort = returnRequestRepositoryPort;
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public ReturnRequest issueRefund(User user, ReturnRequest request) {
        validateUserAuthorizationService.requireRole(user, UserRole.ADMIN);
        ReturnRequest stored = returnRequestRepositoryPort.findByIdentifier(request)
                .orElseThrow(() -> new EntityNotFoundException("ReturnRequest", request.getIdentifier()));
        Invoice invoice = invoiceRepositoryPort.findByOrder(stored.getOrder())
                .orElseThrow(() -> new EntityNotFoundException("Invoice for order", stored.getOrder().getIdentifier()));
        Refund refund = stored.issueRefund(user);
        refund.setIdentifier("REF-" + stored.getIdentifier());
        invoice.registerRefund(refund.getAmount());
        invoiceRepositoryPort.update(invoice);
        returnRequestRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.REFUND_ISSUANCE, user, stored,
                AuditDetails.of("refund", refund.getIdentifier(), "amount", refund.getAmount().toString(),
                        "invoice", invoice.getIdentifier(), "invoiceStatus", invoice.getStatus().getCode()));
        return stored;
    }
}
