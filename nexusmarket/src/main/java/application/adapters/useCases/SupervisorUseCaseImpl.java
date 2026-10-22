package application.adapters.useCases;

import application.domain.models.AdministrativeReport;
import application.domain.models.AuditLog;
import application.domain.models.InventoryItem;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.SupervisorPort;
import application.domain.services.catalog.ConsultCatalogService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.operation.ConsultOperationsService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.report.ConsultAdministrativeReportService;
import application.domain.services.returns.ConsultReturnsService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ReturnStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupervisorUseCaseImpl implements SupervisorPort {

    private final ConsultAdministrativeReportService consultAdministrativeReportService;
    private final ConsultOrdersService consultOrdersService;
    private final ConsultCatalogService consultCatalogService;
    private final ConsultWarehousesService consultWarehousesService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultReturnsService consultReturnsService;
    private final ConsultOperationsService consultOperationsService;

    public SupervisorUseCaseImpl(ConsultAdministrativeReportService consultAdministrativeReportService,
                                 ConsultOrdersService consultOrdersService,
                                 ConsultCatalogService consultCatalogService,
                                 ConsultWarehousesService consultWarehousesService,
                                 ConsultInventoryService consultInventoryService,
                                 ConsultReturnsService consultReturnsService,
                                 ConsultOperationsService consultOperationsService) {
        this.consultAdministrativeReportService = consultAdministrativeReportService;
        this.consultOrdersService = consultOrdersService;
        this.consultCatalogService = consultCatalogService;
        this.consultWarehousesService = consultWarehousesService;
        this.consultInventoryService = consultInventoryService;
        this.consultReturnsService = consultReturnsService;
        this.consultOperationsService = consultOperationsService;
    }

    @Override
    @Transactional(readOnly = true)
    public AdministrativeReport consultAdministrativeReport(User user) {
        return consultAdministrativeReportService.generate(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> consultAllOrders(User user) {
        return consultOrdersService.consultAll(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Order consultOrder(User user, Order order) {
        return consultOrdersService.consultAnyOrder(user, order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> consultAllProducts(User user) {
        return consultCatalogService.consultAll(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> consultWarehouses(User user) {
        return consultWarehousesService.consultAll(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItem> consultWarehouseInventory(User user, Warehouse warehouse) {
        return consultInventoryService.consultByWarehouse(user, warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequest> consultReturns(User user, ReturnStatus status) {
        return consultReturnsService.consultByStatus(user, status == null ? ReturnStatus.REQUESTED : status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Operation> consultOperations(User user) {
        return consultOperationsService.consultOperations(user);
    }

    @Override
    public List<AuditLog> consultAuditLog(User user, OperationType operationType) {
        return consultOperationsService.consultAuditLog(user, operationType);
    }
}
