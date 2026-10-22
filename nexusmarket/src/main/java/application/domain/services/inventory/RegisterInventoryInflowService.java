package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidInventoryOperationException;
import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ProductStatus;
import java.util.Optional;

/**
 * Registers stock received in a warehouse (INFLOW). Allowed to the LOGISTIC_OPERATOR
 * and to the SELLER for its own products. Inventory is always linked to one product
 * variant and one warehouse; the stock record is created on the first inflow.
 */
public class RegisterInventoryInflowService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final InventoryMovementRepositoryPort inventoryMovementRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final InventoryAccessService inventoryAccessService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public RegisterInventoryInflowService(InventoryRepositoryPort inventoryRepositoryPort,
                                          InventoryMovementRepositoryPort inventoryMovementRepositoryPort,
                                          WarehouseRepositoryPort warehouseRepositoryPort,
                                          ProductRepositoryPort productRepositoryPort,
                                          InventoryAccessService inventoryAccessService,
                                          RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.inventoryMovementRepositoryPort = inventoryMovementRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.inventoryAccessService = inventoryAccessService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public InventoryItem registerInflow(User user, Warehouse warehouse, ProductVariant variant, int quantity) {
        inventoryAccessService.requireInventoryRole(user);
        Product product = productRepositoryPort.findByVariantSku(variant)
                .orElseThrow(() -> new EntityNotFoundException("Product variant", variant.getSku()));
        inventoryAccessService.requireInventoryAdministrator(user, product);
        Warehouse storedWarehouse = warehouseRepositoryPort.findByIdentifier(warehouse)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouse.getIdentifier()));
        if (ProductStatus.DISCONTINUED.equals(product.getStatus())) {
            throw new InvalidInventoryOperationException("Discontinued products cannot receive stock.");
        }
        ProductVariant storedVariant = product.requireVariant(variant.getSku());

        InventoryItem probe = new InventoryItem();
        probe.setIdentifier(InventoryItem.buildIdentifier(storedWarehouse.getIdentifier(), storedVariant.getSku()));
        Optional<InventoryItem> existing = inventoryRepositoryPort.findByIdentifier(probe);
        InventoryItem item = existing.orElseGet(() -> InventoryItem.open(storedWarehouse, product, storedVariant));
        item.setWarehouse(storedWarehouse);
        item.registerInflow(quantity);
        InventoryItem saved;
        if (existing.isPresent()) {
            inventoryRepositoryPort.update(item);
            saved = item;
        } else {
            saved = inventoryRepositoryPort.save(item);
        }
        inventoryMovementRepositoryPort.save(
                InventoryMovement.record(saved, MovementType.INFLOW, quantity, user, null, null));
        registerOperationAndAuditService.register(OperationType.INVENTORY_INFLOW, user, saved,
                AuditDetails.of("warehouse", storedWarehouse.getIdentifier(), "sku", storedVariant.getSku(),
                        "quantity", quantity, "availableAfter", saved.getAvailableQuantity()));
        return saved;
    }
}
