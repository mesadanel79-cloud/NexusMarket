package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;

/**
 * Suspends, reactivates or discontinues a product.
 *
 * <p>Only the SELLER that owns the product manages its catalog status (RG-03).
 */
public class ChangeProductStatusService {

    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ChangeProductStatusService(ProductRepositoryPort productRepositoryPort,
                                      ValidateOwnershipService validateOwnershipService,
                                      RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.productRepositoryPort = productRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Product suspend(User user, Product product) {
        Product stored = authorize(user, product);
        String previous = stored.getStatus().getCode();
        stored.suspend();
        return persist(user, stored, OperationType.PRODUCT_SUSPENSION, previous);
    }

    public Product reactivate(User user, Product product) {
        Product stored = authorize(user, product);
        String previous = stored.getStatus().getCode();
        stored.reactivate();
        return persist(user, stored, OperationType.PRODUCT_REACTIVATION, previous);
    }

    public Product discontinue(User user, Product product) {
        Product stored = authorize(user, product);
        String previous = stored.getStatus().getCode();
        stored.discontinue();
        return persist(user, stored, OperationType.PRODUCT_DISCONTINUATION, previous);
    }

    private Product authorize(User user, Product product) {
        Seller seller = validateOwnershipService.requireSeller(user);
        Product stored = productRepositoryPort.findByIdentifier(product)
                .orElseThrow(() -> new EntityNotFoundException("Product", product.getIdentifier()));
        validateOwnershipService.requireProductOwnership(seller, stored);
        return stored;
    }

    private Product persist(User user, Product stored, OperationType type, String previous) {
        productRepositoryPort.update(stored);
        registerOperationAndAuditService.register(type, user, stored,
                AuditDetails.of("previousStatus", previous, "newStatus", stored.getStatus().getCode()));
        return stored;
    }
}
