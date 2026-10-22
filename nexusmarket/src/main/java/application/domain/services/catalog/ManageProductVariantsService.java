package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidProductException;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OperationType;

/**
 * Adds variants and changes variant prices of the seller's own products.
 */
public class ManageProductVariantsService {

    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ManageProductVariantsService(ProductRepositoryPort productRepositoryPort,
                                        ValidateOwnershipService validateOwnershipService,
                                        RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.productRepositoryPort = productRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Product addVariant(User user, Product product, ProductVariant variant) {
        Seller seller = validateOwnershipService.requireSeller(user);
        Product stored = load(product);
        validateOwnershipService.requireProductOwnership(seller, stored);
        if (variant != null && productRepositoryPort.existsBySku(variant)) {
            throw new InvalidProductException("SKU " + variant.getSku() + " already exists in the marketplace.");
        }
        stored.addVariant(variant);
        productRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.PRODUCT_VARIANT_ADDITION, user, stored,
                AuditDetails.of("sku", variant.getSku(), "price", variant.getPrice().toString()));
        return stored;
    }

    public Product changeVariantPrice(User user, Product product, String sku, Money newPrice) {
        Seller seller = validateOwnershipService.requireSeller(user);
        Product stored = load(product);
        validateOwnershipService.requireProductOwnership(seller, stored);
        String previous = stored.requireVariant(sku).getPrice().toString();
        stored.changeVariantPrice(sku, newPrice);
        productRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.PRODUCT_PRICE_CHANGE, user, stored,
                AuditDetails.of("sku", sku, "previousPrice", previous, "newPrice", newPrice.toString()));
        return stored;
    }

    private Product load(Product product) {
        return productRepositoryPort.findByIdentifier(product)
                .orElseThrow(() -> new EntityNotFoundException("Product", product.getIdentifier()));
    }
}
