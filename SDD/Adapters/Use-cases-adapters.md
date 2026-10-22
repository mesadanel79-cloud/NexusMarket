# Use Case Adapters

Ubicación: `adapters/useCases`. Cada clase implementa **un puerto de entrada por rol**, está anotada con `@Service` y define la transacción (`@Transactional`, `readOnly = true` en consultas). No contienen reglas de negocio: delegan en los servicios de dominio.

| Clase | Implementa | Servicios que inyecta |
| --- | --- | --- |
| `PublicAccessUseCaseImpl` | `PublicAccessPort` | LoginService, LogoutService, RegisterBuyerService, ConsultCatalogService |
| `BuyerUseCaseImpl` | `BuyerPort` | ConsultBuyerProfileService, UpdateBuyerProfileService, ChangePasswordService, ConsultCartService, ManageCartService, CheckoutService, PayOrderService, CancelOrderService, ConsultOrdersService, ConsultShipmentsService, ConsultInvoicesService, RequestReturnService, ConsultReturnsService |
| `SellerUseCaseImpl` | `SellerPort` | ConsultSellersService, ChangePasswordService, CreateProductService, ConsultCatalogService, ManageProductVariantsService, ChangeProductStatusService, ConsultInventoryService, ConsultOrdersService, RegisterInventoryInflowService, AdjustInventoryService |
| `LogisticOperatorUseCaseImpl` | `LogisticOperatorPort` | ConsultWarehousesService, ConsultInventoryService, RegisterInventoryInflowService, AdjustInventoryService, ConsultOrdersService, DispatchOrderService, ConfirmDeliveryService, ConsultShipmentsService, ConsultReturnsService, ReceiveReturnService |
| `SupervisorUseCaseImpl` | `SupervisorPort` | ConsultAdministrativeReportService, ConsultOrdersService, ConsultCatalogService, ConsultWarehousesService, ConsultInventoryService, ConsultReturnsService, ConsultOperationsService |
| `AdminUseCaseImpl` | `AdminPort` | RegisterSellerService, RegisterStaffUserService, ConsultUsersService, ChangeUserStatusService, ChangeBuyerStatusService, ConsultSellersService, ChangeSellerStatusService, RegisterWarehouseService, ConsultWarehousesService, ChangeWarehouseStatusService, ConsultReturnsService, ReviewReturnService, IssueRefundService, ConsultAdministrativeReportService, ConsultOperationsService |
