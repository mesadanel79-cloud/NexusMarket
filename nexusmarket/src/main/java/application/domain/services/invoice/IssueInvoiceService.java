package application.domain.services.invoice;

import application.domain.exceptions.InvalidInvoiceException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.BusinessConfigurationPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;

/**
 * Issues the single invoice of a paid order. Invoked by the payment flow.
 */
public class IssueInvoiceService {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final BusinessConfigurationPort businessConfigurationPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public IssueInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort,
                               BusinessConfigurationPort businessConfigurationPort,
                               RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.businessConfigurationPort = businessConfigurationPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Invoice issue(User user, Order paidOrder) {
        if (invoiceRepositoryPort.findByOrder(paidOrder).isPresent()) {
            throw new InvalidInvoiceException("Order " + paidOrder.getIdentifier() + " already has an invoice.");
        }
        Invoice invoice = Invoice.issueFor(paidOrder, businessConfigurationPort.getTaxRate());
        Invoice saved = invoiceRepositoryPort.save(invoice);
        registerOperationAndAuditService.register(OperationType.INVOICE_ISSUANCE, user, saved,
                AuditDetails.of("order", paidOrder.getIdentifier(), "subtotal", saved.getSubtotal().toString(),
                        "tax", saved.getTaxAmount().toString(), "total", saved.getTotal().toString()));
        return saved;
    }
}
