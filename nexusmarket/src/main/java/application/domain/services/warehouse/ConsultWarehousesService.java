package application.domain.services.warehouse;

import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Lists warehouses (staff roles).
 */
public class ConsultWarehousesService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultWarehousesService(WarehouseRepositoryPort warehouseRepositoryPort,
                                    ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    public List<Warehouse> consultAll(User user) {
        validateUserAuthorizationService.requireRole(user,
                UserRole.ADMIN, UserRole.LOGISTIC_OPERATOR, UserRole.SUPERVISOR);
        return warehouseRepositoryPort.findAll();
    }
}
