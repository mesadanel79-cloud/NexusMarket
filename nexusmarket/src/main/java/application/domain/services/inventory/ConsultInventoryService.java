package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Inventory queries: per warehouse and movements (staff), per product (owner seller).
 */
public class ConsultInventoryService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final InventoryMovementRepositoryPort inventoryMovementRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final ValidateOwnershipService validateOwnershipService;

    public ConsultInventoryService(InventoryRepositoryPort inventoryRepositoryPort,
                                   InventoryMovementRepositoryPort inventoryMovementRepositoryPort,
                                   WarehouseRepositoryPort warehouseRepositoryPort,
                                   ProductRepositoryPort productRepositoryPort,
                                   ValidateUserAuthorizationService validateUserAuthorizationService,
                                   ValidateOwnershipService validateOwnershipService) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.inventoryMovementRepositoryPort = inventoryMovementRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.validateOwnershipService = validateOwnershipService;
    }

    public List<InventoryItem> consultByWarehouse(User user, Warehouse warehouse) {
        validateUserAuthorizationService.requireRole(user,
                UserRole.LOGISTIC_OPERATOR, UserRole.SUPERVISOR, UserRole.ADMIN);
        Warehouse stored = warehouseRepositoryPort.findByIdentifier(warehouse)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouse.getIdentifier()));
        return inventoryRepositoryPort.findByWarehouse(stored);
    }

    public List<InventoryItem> consultByProduct(User user, Product product) {
        Seller seller = validateOwnershipService.requireSeller(user);
        Product stored = productRepositoryPort.findByIdentifier(product)
                .orElseThrow(() -> new EntityNotFoundException("Product", product.getIdentifier()));
        validateOwnershipService.requireProductOwnership(seller, stored);
        return inventoryRepositoryPort.findByProduct(stored);
    }

    public List<InventoryMovement> consultMovements(User user, InventoryItem item) {
        validateUserAuthorizationService.requireRole(user,
                UserRole.LOGISTIC_OPERATOR, UserRole.SUPERVISOR, UserRole.ADMIN);
        InventoryItem stored = inventoryRepositoryPort.findByIdentifier(item)
                .orElseThrow(() -> new EntityNotFoundException("InventoryItem", item.getIdentifier()));
        return inventoryMovementRepositoryPort.findByInventoryItem(stored);
    }
}
