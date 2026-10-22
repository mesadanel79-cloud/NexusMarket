# API REST Endpoints — NexusMarket

Base URL: `http://localhost:8080`. Todas las rutas protegidas requieren `Authorization: Bearer <jwt>`; `Content-Type: application/json` en POST/PUT/PATCH. Cada prefijo pertenece a **un solo rol** (un puerto de entrada por rol). Errores: ver `Global-exception-handler.md`. Este documento se generó a partir de los controladores, por lo que coincide 1:1 con el código.

| Prefijo | Rol | Puerto de entrada |
| --- | --- | --- |
| `/api/v1/auth` | Público (logout requiere token) | `PublicAccessPort` |
| `/api/v1/catalog` | Público | `PublicAccessPort` |
| `/api/v1/buyer` | BUYER | `BuyerPort` |
| `/api/v1/seller` | SELLER | `SellerPort` |
| `/api/v1/logistics` | LOGISTIC_OPERATOR | `LogisticOperatorPort` |
| `/api/v1/supervisor` | SUPERVISOR (solo GET) | `SupervisorPort` |
| `/api/v1/admin` | ADMIN | `AdminPort` |

## AuthRestController — `/api/v1/auth`

| Método | Ruta | Request body | Respuesta | Operación del puerto |
| --- | --- | --- | --- | --- |
| POST | `/api/v1/auth/login` | `LoginRequestDTO` | `LoginResponseDTO` (200) | `login` |
| POST | `/api/v1/auth/logout` | — | `Void` (204) | `logout` |
| POST | `/api/v1/auth/register/buyer` | `RegisterBuyerRequestDTO` | `UserResponseDTO` (201) | `registerBuyer` |

## CatalogRestController — `/api/v1/catalog`

| Método | Ruta | Request body | Respuesta | Operación del puerto |
| --- | --- | --- | --- | --- |
| GET | `/api/v1/catalog/products ?category=` | — | `List‹ProductResponseDTO›` (200) | `consultCatalog` |
| GET | `/api/v1/catalog/products/{productId}` | — | `ProductResponseDTO` (200) | `consultProduct` |

## BuyerRestController — `/api/v1/buyer`

| Método | Ruta | Request body | Respuesta | Operación del puerto |
| --- | --- | --- | --- | --- |
| GET | `/api/v1/buyer/profile` | — | `BuyerResponseDTO` (200) | `consultMyProfile` |
| PUT | `/api/v1/buyer/profile` | `UpdateBuyerProfileRequestDTO` | `BuyerResponseDTO` (200) | `updateMyProfile` |
| PATCH | `/api/v1/buyer/password` | `ChangePasswordRequestDTO` | `Void` (204) | `changeMyPassword` |
| GET | `/api/v1/buyer/cart` | — | `CartResponseDTO` (200) | `consultMyCart` |
| POST | `/api/v1/buyer/cart/items` | `CartItemRequestDTO` | `CartResponseDTO` (200) | `addItemToCart` |
| PATCH | `/api/v1/buyer/cart/items/{sku} ?quantity=` | — | `CartResponseDTO` (200) | `updateCartItem` |
| DELETE | `/api/v1/buyer/cart/items/{sku}` | — | `CartResponseDTO` (200) | `removeCartItem` |
| POST | `/api/v1/buyer/orders` | `CheckoutRequestDTO` | `OrderResponseDTO` (201) | `checkout` |
| GET | `/api/v1/buyer/orders` | — | `List‹OrderResponseDTO›` (200) | `consultMyOrders` |
| GET | `/api/v1/buyer/orders/{orderId}` | — | `OrderResponseDTO` (200) | `consultMyOrder` |
| POST | `/api/v1/buyer/orders/{orderId}/payment` | `PayOrderRequestDTO` | `OrderResponseDTO` (200) | `payOrder` |
| PATCH | `/api/v1/buyer/orders/{orderId}/cancel` | — | `OrderResponseDTO` (200) | `cancelOrder` |
| GET | `/api/v1/buyer/orders/{orderId}/shipment` | — | `ShipmentResponseDTO` (200) | `consultOrderShipment` |
| GET | `/api/v1/buyer/invoices` | — | `List‹InvoiceResponseDTO›` (200) | `consultMyInvoices` |
| GET | `/api/v1/buyer/invoices/{invoiceId}` | — | `InvoiceResponseDTO` (200) | `consultMyInvoice` |
| POST | `/api/v1/buyer/returns` | `CreateReturnRequestDTO` | `ReturnRequestResponseDTO` (201) | `requestReturn` |
| GET | `/api/v1/buyer/returns` | — | `List‹ReturnRequestResponseDTO›` (200) | `consultMyReturns` |

## SellerRestController — `/api/v1/seller`

| Método | Ruta | Request body | Respuesta | Operación del puerto |
| --- | --- | --- | --- | --- |
| GET | `/api/v1/seller/store` | — | `SellerResponseDTO` (200) | `consultMyStore` |
| PATCH | `/api/v1/seller/password` | `ChangePasswordRequestDTO` | `Void` (204) | `changeMyPassword` |
| POST | `/api/v1/seller/products` | `CreateProductRequestDTO` | `ProductResponseDTO` (201) | `createProduct` |
| GET | `/api/v1/seller/products` | — | `List‹ProductResponseDTO›` (200) | `consultMyProducts` |
| POST | `/api/v1/seller/products/{productId}/variants` | `VariantRequestDTO` | `ProductResponseDTO` (200) | `addVariant` |
| PATCH | `/api/v1/seller/products/{productId}/variants/{sku}/price` | `ChangePriceRequestDTO` | `ProductResponseDTO` (200) | `changeVariantPrice` |
| PATCH | `/api/v1/seller/products/{productId}/suspend` | — | `ProductResponseDTO` (200) | `suspendProduct` |
| PATCH | `/api/v1/seller/products/{productId}/reactivate` | — | `ProductResponseDTO` (200) | `reactivateProduct` |
| PATCH | `/api/v1/seller/products/{productId}/discontinue` | — | `ProductResponseDTO` (200) | `discontinueProduct` |
| GET | `/api/v1/seller/products/{productId}/inventory` | — | `List‹InventoryItemResponseDTO›` (200) | `consultProductInventory` |
| POST | `/api/v1/seller/inventory/inflows` | `InventoryInflowRequestDTO` | `InventoryItemResponseDTO` (201) | `registerInventoryInflow` |
| PATCH | `/api/v1/seller/inventory/{inventoryItemId}/adjustment` | `InventoryAdjustmentRequestDTO` | `InventoryItemResponseDTO` (200) | `adjustInventory` |
| PATCH | `/api/v1/seller/inventory/{inventoryItemId}/damaged` | `DamagedInventoryRequestDTO` | `InventoryItemResponseDTO` (200) | `markDamagedInventory` |
| GET | `/api/v1/seller/sales` | — | `List‹OrderResponseDTO›` (200) | `consultMySales` |

## LogisticOperatorRestController — `/api/v1/logistics`

| Método | Ruta | Request body | Respuesta | Operación del puerto |
| --- | --- | --- | --- | --- |
| GET | `/api/v1/logistics/warehouses` | — | `List‹WarehouseResponseDTO›` (200) | `consultWarehouses` |
| GET | `/api/v1/logistics/warehouses/{warehouseId}/inventory` | — | `List‹InventoryItemResponseDTO›` (200) | `consultWarehouseInventory` |
| POST | `/api/v1/logistics/inventory/inflows` | `InventoryInflowRequestDTO` | `InventoryItemResponseDTO` (201) | `registerInventoryInflow` |
| PATCH | `/api/v1/logistics/inventory/{inventoryItemId}/adjustment` | `InventoryAdjustmentRequestDTO` | `InventoryItemResponseDTO` (200) | `adjustInventory` |
| PATCH | `/api/v1/logistics/inventory/{inventoryItemId}/damaged` | `DamagedInventoryRequestDTO` | `InventoryItemResponseDTO` (200) | `markDamagedInventory` |
| GET | `/api/v1/logistics/inventory/{inventoryItemId}/movements` | — | `List‹InventoryMovementResponseDTO›` (200) | `consultInventoryMovements` |
| GET | `/api/v1/logistics/orders/ready-to-ship` | — | `List‹OrderResponseDTO›` (200) | `consultOrdersReadyToShip` |
| POST | `/api/v1/logistics/orders/{orderId}/shipment` | `DispatchOrderRequestDTO` | `ShipmentResponseDTO` (201) | `dispatchOrder` |
| PATCH | `/api/v1/logistics/shipments/{shipmentId}/delivery` | — | `ShipmentResponseDTO` (200) | `confirmDelivery` |
| GET | `/api/v1/logistics/shipments ?status=` | — | `List‹ShipmentResponseDTO›` (200) | `consultShipments` |
| GET | `/api/v1/logistics/returns/approved` | — | `List‹ReturnRequestResponseDTO›` (200) | `consultApprovedReturns` |
| PATCH | `/api/v1/logistics/returns/{returnId}/reception` | — | `ReturnRequestResponseDTO` (200) | `receiveReturn` |

## SupervisorRestController — `/api/v1/supervisor`

| Método | Ruta | Request body | Respuesta | Operación del puerto |
| --- | --- | --- | --- | --- |
| GET | `/api/v1/supervisor/reports/summary` | — | `AdministrativeReportResponseDTO` (200) | `consultAdministrativeReport` |
| GET | `/api/v1/supervisor/orders` | — | `List‹OrderResponseDTO›` (200) | `consultAllOrders` |
| GET | `/api/v1/supervisor/orders/{orderId}` | — | `OrderResponseDTO` (200) | `consultOrder` |
| GET | `/api/v1/supervisor/products` | — | `List‹ProductResponseDTO›` (200) | `consultAllProducts` |
| GET | `/api/v1/supervisor/warehouses` | — | `List‹WarehouseResponseDTO›` (200) | `consultWarehouses` |
| GET | `/api/v1/supervisor/warehouses/{warehouseId}/inventory` | — | `List‹InventoryItemResponseDTO›` (200) | `consultWarehouseInventory` |
| GET | `/api/v1/supervisor/returns ?status=` | — | `List‹ReturnRequestResponseDTO›` (200) | `consultReturns` |
| GET | `/api/v1/supervisor/operations` | — | `List‹OperationResponseDTO›` (200) | `consultOperations` |
| GET | `/api/v1/supervisor/audit-logs ?operationType=` | — | `List‹AuditLogResponseDTO›` (200) | `consultAuditLog` |

## AdminRestController — `/api/v1/admin`

| Método | Ruta | Request body | Respuesta | Operación del puerto |
| --- | --- | --- | --- | --- |
| POST | `/api/v1/admin/sellers` | `RegisterSellerRequestDTO` | `UserResponseDTO` (201) | `registerSeller` |
| GET | `/api/v1/admin/sellers` | — | `List‹SellerResponseDTO›` (200) | `consultSellers` |
| PATCH | `/api/v1/admin/sellers/{identification}/status` | `ChangeStatusRequestDTO` | `SellerResponseDTO` (200) | `changeSellerStatus` |
| PATCH | `/api/v1/admin/buyers/{identification}/status` | `ChangeStatusRequestDTO` | `BuyerResponseDTO` (200) | `changeBuyerStatus` |
| POST | `/api/v1/admin/users` | `RegisterStaffUserRequestDTO` | `UserResponseDTO` (201) | `registerStaffUser` |
| GET | `/api/v1/admin/users` | — | `List‹UserResponseDTO›` (200) | `consultUsers` |
| PATCH | `/api/v1/admin/users/{userId}/status` | `ChangeStatusRequestDTO` | `UserResponseDTO` (200) | `changeUserStatus` |
| POST | `/api/v1/admin/warehouses` | `WarehouseRequestDTO` | `WarehouseResponseDTO` (201) | `registerWarehouse` |
| GET | `/api/v1/admin/warehouses` | — | `List‹WarehouseResponseDTO›` (200) | `consultWarehouses` |
| PATCH | `/api/v1/admin/warehouses/{warehouseId}/status` | `ChangeStatusRequestDTO` | `WarehouseResponseDTO` (200) | `changeWarehouseStatus` |
| GET | `/api/v1/admin/returns ?status=` | — | `List‹ReturnRequestResponseDTO›` (200) | `consultReturns` |
| PATCH | `/api/v1/admin/returns/{returnId}/review` | `ReviewReturnRequestDTO` | `ReturnRequestResponseDTO` (200) | `reviewReturn` |
| POST | `/api/v1/admin/returns/{returnId}/refund` | — | `ReturnRequestResponseDTO` (200) | `issueRefund` |
| GET | `/api/v1/admin/reports/summary` | — | `AdministrativeReportResponseDTO` (200) | `consultAdministrativeReport` |
| GET | `/api/v1/admin/audit-logs ?operationType=` | — | `List‹AuditLogResponseDTO›` (200) | `consultAuditLog` |

# DTOs

Todos los DTO son `record` de Java con Jakarta Bean Validation. Los códigos de catálogo se validan con `@Pattern` y se convierten a Value Objects con `fromCode`.

## Requests

### CartItemRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| sku | `@NotBlank String` |
| quantity | `@NotNull @Positive Integer` |

### ChangePasswordRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| currentPassword | `@NotBlank String` |
| newPassword | `@NotBlank @Size(min = 8, max = 100) String` |

### ChangePriceRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| price | `@NotNull @Positive BigDecimal` |
| currency | `@NotBlank @Pattern(regexp = "COP|USD") String` |

### ChangeStatusRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| status | `@NotBlank String` |

### CheckoutRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| shippingAddress | `@Size(max = 200) String` |

### CreateProductRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| name | `@NotBlank @Size(max = 150) String` |
| description | `@Size(max = 1000) String` |
| category | `@NotBlank @Pattern(regexp = "ELECTRONICS|FASHION|HOME|BOOKS|SPORTS|SOFTWARE|OTHER") String` |
| productType | `@NotBlank @Pattern(regexp = "PHYSICAL|DIGITAL") String` |
| variants | `@NotEmpty List<@Valid VariantRequestDTO>` |

### CreateReturnRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| orderId | `@NotBlank String` |
| sku | `@NotBlank String` |
| quantity | `@NotNull @Positive Integer` |
| reason | `@NotBlank @Pattern(regexp = "DEFECTIVE|WRONG_ITEM|NOT_AS_DESCRIBED|NO_LONGER_NEEDED") String` |
| comment | `@Size(max = 500) String` |

### DamagedInventoryRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| quantity | `@NotNull @Positive Integer` |
| reason | `@NotBlank @Size(max = 200) String` |

### DispatchOrderRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| carrier | `@NotBlank @Size(max = 60) String` |
| trackingNumber | `@NotBlank @Size(max = 60) String` |

### FirstWarehouseRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| code | `@NotBlank @Pattern(regexp = "[A-Z0-9-]{3,20}") String` |
| name | `@NotBlank @Size(max = 100) String` |
| city | `@NotBlank @Size(max = 60) String` |
| address | `@NotBlank @Size(max = 200) String` |

### InventoryAdjustmentRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| newAvailableQuantity | `@NotNull @PositiveOrZero Integer` |
| reason | `@NotBlank @Size(max = 200) String` |

### InventoryInflowRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| warehouseId | `@NotBlank String` |
| sku | `@NotBlank String` |
| quantity | `@NotNull @Positive Integer` |

### LoginRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| username | `@NotBlank String` |
| password | `@NotBlank String` |

### PayOrderRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| paymentMethod | `@NotBlank @Pattern(regexp = "CREDIT_CARD|DEBIT_CARD|BANK_TRANSFER|WALLET") String` |

### RegisterBuyerRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| identification | `@NotBlank @Size(max = 20) String` |
| name | `@NotBlank @Size(max = 100) String` |
| email | `@NotBlank @Email @Size(max = 100) String` |
| phoneNumber | `@NotBlank @Pattern(regexp = "\\d{7,15}", message = "must contain between 7 and 15 digits") String` |
| address | `@NotBlank @Size(max = 200) String` |
| additionalAddresses | `@Size(max = 5) List<@NotBlank @Size(max = 200) String>` |
| username | `@NotBlank @Size(min = 4, max = 50) String` |
| password | `@NotBlank @Size(min = 8, max = 100) String` |

### RegisterSellerRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| identification | `@NotBlank @Size(max = 20) String` |
| name | `@NotBlank @Size(max = 100) String` |
| email | `@NotBlank @Email @Size(max = 100) String` |
| phoneNumber | `@NotBlank @Pattern(regexp = "\\d{7,15}", message = "must contain between 7 and 15 digits") String` |
| address | `@NotBlank @Size(max = 200) String` |
| storeName | `@NotBlank @Size(max = 100) String` |
| username | `@NotBlank @Size(min = 4, max = 50) String` |
| password | `@NotBlank @Size(min = 8, max = 100) String` |
| firstWarehouse | `@NotNull @Valid FirstWarehouseRequestDTO` |

### RegisterStaffUserRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| identification | `@NotBlank @Size(max = 20) String` |
| name | `@NotBlank @Size(max = 100) String` |
| email | `@NotBlank @Email @Size(max = 100) String` |
| phoneNumber | `@NotBlank @Pattern(regexp = "\\d{7,15}", message = "must contain between 7 and 15 digits") String` |
| address | `@NotBlank @Size(max = 200) String` |
| username | `@NotBlank @Size(min = 4, max = 50) String` |
| password | `@NotBlank @Size(min = 8, max = 100) String` |
| role | `@NotBlank @Pattern(regexp = "LOGISTIC_OPERATOR|SUPERVISOR|ADMIN") String` |

### ReviewReturnRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| decision | `@NotBlank @Pattern(regexp = "APPROVED|REJECTED") String` |
| rejectionReason | `@Size(max = 500) String` |

### UpdateBuyerProfileRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| name | `@NotBlank @Size(max = 100) String` |
| email | `@NotBlank @Email @Size(max = 100) String` |
| phoneNumber | `@NotBlank @Pattern(regexp = "\\d{7,15}", message = "must contain between 7 and 15 digits") String` |
| address | `@NotBlank @Size(max = 200) String` |
| additionalAddresses | `@Size(max = 5) List<@NotBlank @Size(max = 200) String>` |

### VariantRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| sku | `@NotBlank @Pattern(regexp = "[A-Z0-9-]{3,40}") String` |
| description | `@NotBlank @Size(max = 150) String` |
| price | `@NotNull @Positive BigDecimal` |
| currency | `@NotBlank @Pattern(regexp = "COP|USD") String` |

### WarehouseRequestDTO

| Campo | Tipo y validación |
| --- | --- |
| code | `@NotBlank @Pattern(regexp = "[A-Z0-9-]{3,20}") String` |
| name | `@NotBlank @Size(max = 100) String` |
| city | `@NotBlank @Size(max = 60) String` |
| address | `@NotBlank @Size(max = 200) String` |
| warehouseType | `@NotBlank @Pattern(regexp = "MARKETPLACE|SELLER") String` |
| ownerSellerIdentification | `@Size(max = 20) String` |

## Responses

### AdministrativeReportResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| generatedAt | `LocalDateTime` |
| usersByRole | `Map<String, Long>` |
| ordersByStatus | `Map<String, Long>` |
| productsByStatus | `Map<String, Long>` |
| returnsByStatus | `Map<String, Long>` |
| totalInvoiced | `BigDecimal` |
| totalRefunded | `BigDecimal` |
| currency | `String` |
| lowStockItems | `List<InventoryItemResponseDTO>` |

### AuditLogResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| auditId | `String` |
| operationType | `String` |
| operationDate | `LocalDateTime` |
| performedByUserId | `Integer` |
| userRole | `String` |
| affectedAssetId | `String` |
| details | `Map<String, Object>` |

### BuyerResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| identification | `String` |
| name | `String` |
| email | `String` |
| phoneNumber | `String` |
| address | `String` |
| additionalAddresses | `List<String>` |
| status | `String` |

### CartItemResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| productId | `String` |
| productName | `String` |
| sku | `String` |
| quantity | `int` |
| unitPrice | `BigDecimal` |
| subtotal | `BigDecimal` |
| currency | `String` |

### CartResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| cartId | `String` |
| items | `List<CartItemResponseDTO>` |
| total | `BigDecimal` |
| currency | `String` |
| updatedAt | `LocalDateTime` |

### InventoryItemResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| inventoryItemId | `String` |
| warehouseId | `String` |
| productId | `String` |
| sku | `String` |
| availableQuantity | `int` |
| reservedQuantity | `int` |
| damagedQuantity | `int` |
| totalQuantity | `int` |

### InventoryMovementResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| movementId | `Long` |
| inventoryItemId | `String` |
| movementType | `String` |
| quantity | `int` |
| occurredAt | `LocalDateTime` |
| performedByUserId | `Integer` |
| orderId | `String` |
| note | `String` |

### InvoiceResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| invoiceId | `String` |
| orderId | `String` |
| buyerIdentification | `String` |
| issueDate | `LocalDateTime` |
| subtotal | `BigDecimal` |
| taxAmount | `BigDecimal` |
| total | `BigDecimal` |
| refundedAmount | `BigDecimal` |
| currency | `String` |
| status | `String` |

### LoginResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| token | `String` |
| tokenType | `String` |
| expiresIn | `long` |
| user | `UserResponseDTO` |

### OperationResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| operationId | `Long` |
| operationType | `String` |
| executionDate | `LocalDateTime` |
| performedByUserId | `Integer` |
| performedByUsername | `String` |
| performedByRole | `String` |
| affectedAssetId | `String` |

### OrderLineResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| lineNumber | `int` |
| productId | `String` |
| productName | `String` |
| productType | `String` |
| sku | `String` |
| sellerIdentification | `String` |
| quantity | `int` |
| unitPrice | `BigDecimal` |
| lineTotal | `BigDecimal` |
| warehouseId | `String` |

### OrderResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| orderId | `String` |
| buyerIdentification | `String` |
| status | `String` |
| totalAmount | `BigDecimal` |
| currency | `String` |
| shippingAddress | `String` |
| paymentMethod | `String` |
| createdAt | `LocalDateTime` |
| paidAt | `LocalDateTime` |
| shippedAt | `LocalDateTime` |
| deliveredAt | `LocalDateTime` |
| cancelledAt | `LocalDateTime` |
| lines | `List<OrderLineResponseDTO>` |

### ProductResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| productId | `String` |
| sellerIdentification | `String` |
| storeName | `String` |
| name | `String` |
| description | `String` |
| category | `String` |
| productType | `String` |
| status | `String` |
| createdAt | `LocalDateTime` |
| variants | `List<VariantResponseDTO>` |

### ReturnRequestResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| returnId | `String` |
| orderId | `String` |
| buyerIdentification | `String` |
| sku | `String` |
| quantity | `int` |
| reason | `String` |
| comment | `String` |
| status | `String` |
| requestedAt | `LocalDateTime` |
| reviewedByUserId | `Integer` |
| reviewedAt | `LocalDateTime` |
| rejectionReason | `String` |
| receivedAt | `LocalDateTime` |
| refundId | `String` |
| refundAmount | `BigDecimal` |
| refundIssuedAt | `LocalDateTime` |

### SellerResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| identification | `String` |
| name | `String` |
| email | `String` |
| phoneNumber | `String` |
| address | `String` |
| storeName | `String` |
| status | `String` |
| registrationDate | `LocalDate` |

### ShipmentResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| shipmentId | `String` |
| orderId | `String` |
| logisticOperatorUserId | `Integer` |
| carrier | `String` |
| trackingNumber | `String` |
| destinationAddress | `String` |
| status | `String` |
| dispatchedAt | `LocalDateTime` |
| deliveredAt | `LocalDateTime` |

### UserResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| userId | `Integer` |
| username | `String` |
| identification | `String` |
| name | `String` |
| email | `String` |
| role | `String` |
| status | `String` |
| buyerIdentification | `String` |
| sellerIdentification | `String` |

### VariantResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| sku | `String` |
| description | `String` |
| price | `BigDecimal` |
| currency | `String` |

### WarehouseResponseDTO

| Campo | Tipo y validación |
| --- | --- |
| code | `String` |
| name | `String` |
| city | `String` |
| address | `String` |
| warehouseType | `String` |
| ownerSellerIdentification | `String` |
| status | `String` |
