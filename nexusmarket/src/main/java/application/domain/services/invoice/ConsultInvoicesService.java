package application.domain.services.invoice;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import java.util.List;

/**
 * Invoice queries of the authenticated buyer.
 */
public class ConsultInvoicesService {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;

    public ConsultInvoicesService(InvoiceRepositoryPort invoiceRepositoryPort,
                                  ValidateOwnershipService validateOwnershipService) {
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
    }

    public List<Invoice> consultBuyerInvoices(User user) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        return invoiceRepositoryPort.findByBuyer(buyer);
    }

    public Invoice consultBuyerInvoice(User user, Invoice invoice) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        Invoice stored = invoiceRepositoryPort.findByIdentifier(invoice)
                .orElseThrow(() -> new EntityNotFoundException("Invoice", invoice.getIdentifier()));
        if (stored.getBuyer() == null || !buyer.getIdentification().equals(stored.getBuyer().getIdentification())) {
            throw new UnauthorizedOperationException("Invoice " + invoice.getIdentifier() + " does not belong to the buyer.");
        }
        return stored;
    }
}
