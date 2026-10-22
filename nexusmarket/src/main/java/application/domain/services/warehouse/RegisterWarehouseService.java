package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidWarehouseException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;

/**
 * Registers a MARKETPLACE or SELLER warehouse (ADMIN only). The warehouse code is
 * unique and a SELLER warehouse must reference an existing seller.
 */
public class RegisterWarehouseService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public RegisterWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort,
                                    SellerRepositoryPort sellerRepositoryPort,
                                    ValidateUserAuthorizationService validateUserAuthorizationService,
                                    RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Warehouse register(User admin, Warehouse warehouse) {
        validateUserAuthorizationService.requireRole(admin, UserRole.ADMIN);
        if (warehouse == null) {
            throw new InvalidWarehouseException("Warehouse data is required.");
        }
        if (WarehouseType.SELLER.equals(warehouse.getWarehouseType()) && warehouse.getOwner() != null) {
            warehouse.setOwner(sellerRepositoryPort.findByIdentification(warehouse.getOwner())
                    .orElseThrow(() -> new EntityNotFoundException("Seller", warehouse.getOwner().getIdentification())));
        }
        warehouse.register();
        if (warehouseRepositoryPort.existsByIdentifier(warehouse)) {
            throw new InvalidWarehouseException("Warehouse " + warehouse.getIdentifier() + " already exists.");
        }
        Warehouse saved = warehouseRepositoryPort.save(warehouse);
        registerOperationAndAuditService.register(OperationType.WAREHOUSE_REGISTRATION, admin, saved,
                AuditDetails.of("name", saved.getName(), "city", saved.getCity(),
                        "type", saved.getWarehouseType().getCode(),
                        "owner", saved.getOwner() == null ? null : saved.getOwner().getIdentification()));
        return saved;
    }
}
