package application.domain.services.catalog;

import application.domain.exceptions.InvalidProductException;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;

/**
 * Creates and publishes a product of the authenticated seller. SKUs are unique in the
 * whole marketplace and the seller must be ACTIVE.
 */
public class CreateProductService {

    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public CreateProductService(ProductRepositoryPort productRepositoryPort,
                                ValidateOwnershipService validateOwnershipService,
                                RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.productRepositoryPort = productRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Product create(User user, Product product) {
        Seller seller = validateOwnershipService.requireSeller(user);
        if (!seller.isActive()) {
            throw new InvalidProductException("Suspended sellers cannot publish products.");
        }
        if (product == null) {
            throw new InvalidProductException("Product data is required.");
        }
        for (ProductVariant variant : product.getVariants()) {
            if (productRepositoryPort.existsBySku(variant)) {
                throw new InvalidProductException("SKU " + variant.getSku() + " already exists in the marketplace.");
            }
        }
        product.setSeller(seller);
        product.publish();
        Product saved = productRepositoryPort.save(product);
        registerOperationAndAuditService.register(OperationType.PRODUCT_CREATION, user, saved,
                AuditDetails.of("name", saved.getName(), "type", saved.getProductType().getCode(),
                        "category", saved.getCategory().getCode(), "variants", saved.getVariants().size()));
        return saved;
    }
}
