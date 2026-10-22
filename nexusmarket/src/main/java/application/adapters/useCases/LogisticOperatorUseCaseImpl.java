package application.adapters.useCases;

import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.ProductVariant;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.LogisticOperatorPort;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.RegisterInventoryInflowService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.returns.ConsultReturnsService;
import application.domain.services.returns.ReceiveReturnService;
import application.domain.services.shipment.ConfirmDeliveryService;
import application.domain.services.shipment.ConsultShipmentsService;
import application.domain.services.shipment.DispatchOrderService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.ShipmentStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogisticOperatorUseCaseImpl implements LogisticOperatorPort {

    private final ConsultWarehousesService consultWarehousesService;
    private final ConsultInventoryService consultInventoryService;
    private final RegisterInventoryInflowService registerInventoryInflowService;
    private final AdjustInventoryService adjustInventoryService;
    private final ConsultOrdersService consultOrdersService;
    private final DispatchOrderService dispatchOrderService;
    private final ConfirmDeliveryService confirmDeliveryService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ConsultReturnsService consultReturnsService;
    private final ReceiveReturnService receiveReturnService;

    public LogisticOperatorUseCaseImpl(ConsultWarehousesService consultWarehousesService,
                                       ConsultInventoryService consultInventoryService,
                                       RegisterInventoryInflowService registerInventoryInflowService,
                                       AdjustInventoryService adjustInventoryService,
                                       ConsultOrdersService consultOrdersService,
                                       DispatchOrderService dispatchOrderService,
                                       ConfirmDeliveryService confirmDeliveryService,
                                       ConsultShipmentsService consultShipmentsService,
                                       ConsultReturnsService consultReturnsService,
                                       ReceiveReturnService receiveReturnService) {
        this.consultWarehousesService = consultWarehousesService;
        this.consultInventoryService = consultInventoryService;
        this.registerInventoryInflowService = registerInventoryInflowService;
        this.adjustInventoryService = adjustInventoryService;
        this.consultOrdersService = consultOrdersService;
        this.dispatchOrderService = dispatchOrderService;
        this.confirmDeliveryService = confirmDeliveryService;
        this.consultShipmentsService = consultShipmentsService;
        this.consultReturnsService = consultReturnsService;
        this.receiveReturnService = receiveReturnService;
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
    @Transactional
    public InventoryItem registerInventoryInflow(User user, Warehouse warehouse, ProductVariant variant, int quantity) {
        return registerInventoryInflowService.registerInflow(user, warehouse, variant, quantity);
    }

    @Override
    @Transactional
    public InventoryItem adjustInventory(User user, InventoryItem item, int newAvailableQuantity, String reason) {
        return adjustInventoryService.adjust(user, item, newAvailableQuantity, reason);
    }

    @Override
    @Transactional
    public InventoryItem markDamagedInventory(User user, InventoryItem item, int quantity, String reason) {
        return adjustInventoryService.markDamaged(user, item, quantity, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovement> consultInventoryMovements(User user, InventoryItem item) {
        return consultInventoryService.consultMovements(user, item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> consultOrdersReadyToShip(User user) {
        return consultOrdersService.consultOrdersReadyToShip(user);
    }

    @Override
    @Transactional
    public Shipment dispatchOrder(User user, Order order, String carrier, String trackingNumber) {
        return dispatchOrderService.dispatch(user, order, carrier, trackingNumber);
    }

    @Override
    @Transactional
    public Shipment confirmDelivery(User user, Shipment shipment) {
        return confirmDeliveryService.confirm(user, shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> consultShipments(User user, ShipmentStatus status) {
        return consultShipmentsService.consultByStatus(user, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequest> consultApprovedReturns(User user) {
        return consultReturnsService.consultByStatus(user, ReturnStatus.APPROVED);
    }

    @Override
    @Transactional
    public ReturnRequest receiveReturn(User user, ReturnRequest returnRequest) {
        return receiveReturnService.receive(user, returnRequest);
    }
}
