package application.support;

import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.buyer.ChangeBuyerStatusService;
import application.domain.services.buyer.UpdateBuyerProfileService;
import application.domain.services.cart.ManageCartService;
import application.domain.services.catalog.ChangeProductStatusService;
import application.domain.services.catalog.ConsultCatalogService;
import application.domain.services.catalog.CreateProductService;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.InventoryAccessService;
import application.domain.services.inventory.InventoryAllocationService;
import application.domain.services.inventory.RegisterInventoryInflowService;
import application.domain.services.invoice.IssueInvoiceService;
import application.domain.services.operation.ConsultOperationsService;
import application.domain.services.operation.RegisterAuditLogService;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.services.operation.RegisterOperationService;
import application.domain.services.order.CancelOrderService;
import application.domain.services.order.CheckoutService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.order.ExpirePendingOrdersService;
import application.domain.services.order.PayOrderService;
import application.domain.services.report.ConsultAdministrativeReportService;
import application.domain.services.returns.IssueRefundService;
import application.domain.services.returns.ReceiveReturnService;
import application.domain.services.returns.RequestReturnService;
import application.domain.services.returns.ReviewReturnService;
import application.domain.services.shipment.ConfirmDeliveryService;
import application.domain.services.shipment.DispatchOrderService;
import application.domain.services.user.ChangeUserStatusService;
import application.domain.services.user.LoginService;
import application.domain.services.user.LogoutService;
import application.domain.services.user.RegisterBuyerService;
import application.domain.services.user.RegisterSellerService;
import application.domain.services.user.RegisterStaffUserService;
import application.domain.services.user.UserRegistrationRulesService;
import application.domain.services.warehouse.RegisterWarehouseService;

/**
 * Wires the domain services with in-memory output ports, mirroring what Spring does
 * in production (constructor injection).
 */
public final class ServiceHarness {

    public final InMemoryRepositories.Users users = new InMemoryRepositories.Users();
    public final InMemoryRepositories.Buyers buyers = new InMemoryRepositories.Buyers();
    public final InMemoryRepositories.Sellers sellers = new InMemoryRepositories.Sellers();
    public final InMemoryRepositories.Warehouses warehouses = new InMemoryRepositories.Warehouses();
    public final InMemoryRepositories.Products products = new InMemoryRepositories.Products();
    public final InMemoryRepositories.Inventory inventory = new InMemoryRepositories.Inventory();
    public final InMemoryRepositories.Carts carts = new InMemoryRepositories.Carts();
    public final InMemoryRepositories.Orders orders = new InMemoryRepositories.Orders();
    public final InMemoryRepositories.Invoices invoices = new InMemoryRepositories.Invoices();
    public final InMemoryRepositories.Shipments shipments = new InMemoryRepositories.Shipments();
    public final InMemoryRepositories.Returns returns = new InMemoryRepositories.Returns();
    public final InMemoryRepositories.Operations operations = new InMemoryRepositories.Operations();
    public final InMemoryRepositories.AuditLogs auditLogs = new InMemoryRepositories.AuditLogs();
    public final InMemoryRepositories.FakePasswords passwords = new InMemoryRepositories.FakePasswords();
    public final InMemoryRepositories.FakeJwt jwt = new InMemoryRepositories.FakeJwt();
    public final InMemoryRepositories.FixedConfiguration configuration = new InMemoryRepositories.FixedConfiguration();

    public final ValidateUserAuthorizationService authorization = new ValidateUserAuthorizationService();
    public final ValidateOwnershipService ownership = new ValidateOwnershipService(authorization, buyers, sellers);
    public final RegisterOperationAndAuditService audit = new RegisterOperationAndAuditService(
            new RegisterOperationService(operations), new RegisterAuditLogService(auditLogs));
    public final UserRegistrationRulesService registrationRules = new UserRegistrationRulesService(users, passwords);

    public final LoginService login = new LoginService(users, passwords, jwt);
    public final LogoutService logout = new LogoutService(users);
    public final RegisterBuyerService registerBuyer = new RegisterBuyerService(buyers, users, registrationRules, audit);
    public final RegisterWarehouseService registerWarehouse =
            new RegisterWarehouseService(warehouses, sellers, authorization, audit);
    public final RegisterSellerService registerSeller =
            new RegisterSellerService(sellers, users, registrationRules, authorization, audit, registerWarehouse);
    public final RegisterStaffUserService registerStaff =
            new RegisterStaffUserService(users, registrationRules, authorization, audit);
    public final ChangeUserStatusService changeUserStatus = new ChangeUserStatusService(users, authorization, audit);
    public final UpdateBuyerProfileService updateBuyerProfile =
            new UpdateBuyerProfileService(ownership, buyers, users, audit);
    public final ChangeBuyerStatusService changeBuyerStatus = new ChangeBuyerStatusService(buyers, authorization, audit);

    public final CreateProductService createProduct = new CreateProductService(products, ownership, audit);
    public final ChangeProductStatusService changeProductStatus =
            new ChangeProductStatusService(products, ownership, audit);
    public final ConsultCatalogService catalog = new ConsultCatalogService(products, ownership, authorization);

    public final InventoryAccessService inventoryAccess = new InventoryAccessService(authorization, ownership);
    public final InventoryAllocationService allocation = new InventoryAllocationService(inventory, inventory);
    public final RegisterInventoryInflowService inflow =
            new RegisterInventoryInflowService(inventory, inventory, warehouses, products, inventoryAccess, audit);
    public final AdjustInventoryService adjustInventory =
            new AdjustInventoryService(inventory, inventory, inventoryAccess, audit);

    public final ManageCartService cart = new ManageCartService(carts, products, ownership, allocation);
    public final CheckoutService checkout = new CheckoutService(carts, orders, ownership, allocation, audit);
    public final IssueInvoiceService issueInvoice = new IssueInvoiceService(invoices, configuration, audit);
    public final PayOrderService pay = new PayOrderService(orders, ownership, allocation, issueInvoice, audit);
    public final CancelOrderService cancel = new CancelOrderService(orders, ownership, allocation, audit);
    public final ExpirePendingOrdersService expire =
            new ExpirePendingOrdersService(orders, configuration, allocation, audit);
    public final ConsultOrdersService consultOrders = new ConsultOrdersService(orders, ownership, authorization);

    public final DispatchOrderService dispatch = new DispatchOrderService(orders, shipments, authorization, audit);
    public final ConfirmDeliveryService deliver = new ConfirmDeliveryService(shipments, orders, authorization, audit);

    public final RequestReturnService requestReturn =
            new RequestReturnService(returns, orders, configuration, ownership, audit);
    public final ReviewReturnService reviewReturn = new ReviewReturnService(returns, authorization, audit);
    public final ReceiveReturnService receiveReturn = new ReceiveReturnService(returns, allocation, authorization, audit);
    public final IssueRefundService refund = new IssueRefundService(returns, invoices, authorization, audit);

    public final ConsultOperationsService consultOperations =
            new ConsultOperationsService(operations, auditLogs, authorization);
    public final ConsultAdministrativeReportService report = new ConsultAdministrativeReportService(
            users, orders, products, returns, invoices, inventory, configuration, authorization);
}
