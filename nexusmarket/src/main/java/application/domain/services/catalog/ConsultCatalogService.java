package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Catalog queries: public view of published products, the seller's own products and
 * the full catalog for staff.
 */
public class ConsultCatalogService {

    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultCatalogService(ProductRepositoryPort productRepositoryPort,
                                 ValidateOwnershipService validateOwnershipService,
                                 ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.productRepositoryPort = productRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    /** Public: published products, optionally filtered by category. */
    public List<Product> consultPublished(ProductCategory category) {
        return productRepositoryPort.findPublished(category);
    }

    /** Public: a single product, only if it is published. */
    public Product consultPublishedProduct(Product product) {
        Product stored = productRepositoryPort.findByIdentifier(product)
                .orElseThrow(() -> new EntityNotFoundException("Product", product.getIdentifier()));
        if (!stored.isPurchasable()) {
            throw new EntityNotFoundException("Product", product.getIdentifier());
        }
        return stored;
    }

    public List<Product> consultSellerProducts(User user) {
        Seller seller = validateOwnershipService.requireSeller(user);
        return productRepositoryPort.findBySeller(seller);
    }

    public List<Product> consultAll(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.SUPERVISOR, UserRole.ADMIN);
        return productRepositoryPort.findAll();
    }
}
