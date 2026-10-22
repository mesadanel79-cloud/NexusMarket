package application.support;

import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.ReturnRequest;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.ports.out.BusinessConfigurationPort;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.OperationRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ReturnRequestRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.ShipmentStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory test doubles of every output port, so domain services can be tested
 * without MySQL, MongoDB or Spring.
 */
public final class InMemoryRepositories {

    private InMemoryRepositories() {
    }

    private static int sequence = 1;

    static synchronized String nextId(String prefix) {
        return prefix + "-" + (sequence++);
    }

    public static class Users implements UserRepositoryPort {
        public final Map<Integer, User> store = new LinkedHashMap<>();
        private int ids = 1;

        public User save(User user) {
            user.setUserId(ids++);
            store.put(user.getUserId(), user);
            return user;
        }

        public void update(User user) {
            store.put(user.getUserId(), user);
        }

        public Optional<User> findById(User user) {
            return Optional.ofNullable(store.get(user.getUserId()));
        }

        public Optional<User> findByUsername(User user) {
            return store.values().stream().filter(u -> u.getUsername().equals(user.getUsername())).findFirst();
        }

        public boolean existsByUsername(User user) {
            return findByUsername(user).isPresent();
        }

        public boolean existsByIdentification(User user) {
            return store.values().stream().anyMatch(u -> u.getIdentification().equals(user.getIdentification()));
        }

        public boolean existsByEmail(User user) {
            return store.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        }

        public List<User> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    public static class Buyers implements BuyerRepositoryPort {
        public final Map<String, Buyer> store = new LinkedHashMap<>();

        public Buyer save(Buyer buyer) {
            store.put(buyer.getIdentification(), buyer);
            return buyer;
        }

        public void update(Buyer buyer) {
            save(buyer);
        }

        public Optional<Buyer> findByIdentification(Buyer buyer) {
            return Optional.ofNullable(store.get(buyer.getIdentification()));
        }

        public boolean existsByIdentification(Buyer buyer) {
            return store.containsKey(buyer.getIdentification());
        }

        public boolean existsByEmail(Buyer buyer) {
            return store.values().stream().anyMatch(b -> b.getEmail().equalsIgnoreCase(buyer.getEmail()));
        }
    }

    public static class Sellers implements SellerRepositoryPort {
        public final Map<String, Seller> store = new LinkedHashMap<>();

        public Seller save(Seller seller) {
            store.put(seller.getIdentification(), seller);
            return seller;
        }

        public void update(Seller seller) {
            save(seller);
        }

        public Optional<Seller> findByIdentification(Seller seller) {
            return Optional.ofNullable(store.get(seller.getIdentification()));
        }

        public boolean existsByIdentification(Seller seller) {
            return store.containsKey(seller.getIdentification());
        }

        public boolean existsByEmail(Seller seller) {
            return store.values().stream().anyMatch(s -> s.getEmail().equalsIgnoreCase(seller.getEmail()));
        }

        public List<Seller> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    public static class Warehouses implements WarehouseRepositoryPort {
        public final Map<String, Warehouse> store = new LinkedHashMap<>();

        public Warehouse save(Warehouse warehouse) {
            store.put(warehouse.getIdentifier(), warehouse);
            return warehouse;
        }

        public void update(Warehouse warehouse) {
            save(warehouse);
        }

        public Optional<Warehouse> findByIdentifier(Warehouse warehouse) {
            return Optional.ofNullable(store.get(warehouse.getIdentifier()));
        }

        public boolean existsByIdentifier(Warehouse warehouse) {
            return store.containsKey(warehouse.getIdentifier());
        }

        public List<Warehouse> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    public static class Products implements ProductRepositoryPort {
        public final Map<String, Product> store = new LinkedHashMap<>();

        public Product save(Product product) {
            if (product.getIdentifier() == null) {
                product.setIdentifier(nextId("PRD"));
            }
            store.put(product.getIdentifier(), product);
            return product;
        }

        public void update(Product product) {
            store.put(product.getIdentifier(), product);
        }

        public Optional<Product> findByIdentifier(Product product) {
            return Optional.ofNullable(store.get(product.getIdentifier()));
        }

        public Optional<Product> findByVariantSku(ProductVariant variant) {
            return store.values().stream().filter(p -> p.findVariant(variant.getSku()).isPresent()).findFirst();
        }

        public boolean existsBySku(ProductVariant variant) {
            return findByVariantSku(variant).isPresent();
        }

        public List<Product> findBySeller(Seller seller) {
            return store.values().stream().filter(p -> p.isOwnedBy(seller)).toList();
        }

        public List<Product> findPublished(ProductCategory category) {
            return store.values().stream()
                    .filter(p -> ProductStatus.PUBLISHED.equals(p.getStatus()))
                    .filter(p -> category == null || category.equals(p.getCategory()))
                    .toList();
        }

        public List<Product> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    public static class Inventory implements InventoryRepositoryPort, InventoryMovementRepositoryPort {
        public final Map<String, InventoryItem> store = new LinkedHashMap<>();
        public final List<InventoryMovement> movements = new ArrayList<>();

        public InventoryItem save(InventoryItem item) {
            store.put(item.getIdentifier(), item);
            return item;
        }

        public void update(InventoryItem item) {
            save(item);
        }

        public Optional<InventoryItem> findByIdentifier(InventoryItem item) {
            return Optional.ofNullable(store.get(item.getIdentifier()));
        }

        public List<InventoryItem> findByVariant(ProductVariant variant) {
            return store.values().stream().filter(i -> i.getVariant().getSku().equals(variant.getSku())).toList();
        }

        public List<InventoryItem> findByWarehouse(Warehouse warehouse) {
            return store.values().stream()
                    .filter(i -> i.getWarehouse().getIdentifier().equals(warehouse.getIdentifier())).toList();
        }

        public List<InventoryItem> findByProduct(Product product) {
            return store.values().stream()
                    .filter(i -> i.getProduct().getIdentifier().equals(product.getIdentifier())).toList();
        }

        public List<InventoryItem> findAll() {
            return new ArrayList<>(store.values());
        }

        public InventoryMovement save(InventoryMovement movement) {
            movement.setMovementId((long) movements.size() + 1);
            movements.add(movement);
            return movement;
        }

        public List<InventoryMovement> findByInventoryItem(InventoryItem item) {
            return movements.stream()
                    .filter(m -> m.getInventoryItem().getIdentifier().equals(item.getIdentifier())).toList();
        }
    }

    public static class Carts implements CartRepositoryPort {
        public final Map<String, Cart> store = new LinkedHashMap<>();

        public Cart save(Cart cart) {
            store.put(cart.getBuyer().getIdentification(), cart);
            return cart;
        }

        public Optional<Cart> findByBuyer(Buyer buyer) {
            return Optional.ofNullable(store.get(buyer.getIdentification()));
        }
    }

    public static class Orders implements OrderRepositoryPort {
        public final Map<String, Order> store = new LinkedHashMap<>();

        public Order save(Order order) {
            if (order.getIdentifier() == null) {
                order.setIdentifier(nextId("ORD"));
            }
            store.put(order.getIdentifier(), order);
            return order;
        }

        public void update(Order order) {
            store.put(order.getIdentifier(), order);
        }

        public Optional<Order> findByIdentifier(Order order) {
            return Optional.ofNullable(store.get(order.getIdentifier()));
        }

        public List<Order> findByBuyer(Buyer buyer) {
            return store.values().stream().filter(o -> o.belongsTo(buyer)).toList();
        }

        public List<Order> findBySeller(Seller seller) {
            return store.values().stream().filter(o -> o.containsProductsOf(seller)).toList();
        }

        public List<Order> findByStatus(OrderStatus status) {
            return store.values().stream().filter(o -> status.equals(o.getStatus())).toList();
        }

        public List<Order> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    public static class Invoices implements InvoiceRepositoryPort {
        public final Map<String, Invoice> store = new LinkedHashMap<>();

        public Invoice save(Invoice invoice) {
            if (invoice.getIdentifier() == null) {
                invoice.setIdentifier(nextId("INVC"));
            }
            store.put(invoice.getIdentifier(), invoice);
            return invoice;
        }

        public void update(Invoice invoice) {
            store.put(invoice.getIdentifier(), invoice);
        }

        public Optional<Invoice> findByIdentifier(Invoice invoice) {
            return Optional.ofNullable(store.get(invoice.getIdentifier()));
        }

        public Optional<Invoice> findByOrder(Order order) {
            return store.values().stream()
                    .filter(i -> i.getOrder().getIdentifier().equals(order.getIdentifier())).findFirst();
        }

        public List<Invoice> findByBuyer(Buyer buyer) {
            return store.values().stream()
                    .filter(i -> i.getBuyer().getIdentification().equals(buyer.getIdentification())).toList();
        }

        public List<Invoice> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    public static class Shipments implements ShipmentRepositoryPort {
        public final Map<String, Shipment> store = new LinkedHashMap<>();

        public Shipment save(Shipment shipment) {
            if (shipment.getIdentifier() == null) {
                shipment.setIdentifier(nextId("SHP"));
            }
            store.put(shipment.getIdentifier(), shipment);
            return shipment;
        }

        public void update(Shipment shipment) {
            store.put(shipment.getIdentifier(), shipment);
        }

        public Optional<Shipment> findByIdentifier(Shipment shipment) {
            return Optional.ofNullable(store.get(shipment.getIdentifier()));
        }

        public Optional<Shipment> findByOrder(Order order) {
            return store.values().stream()
                    .filter(s -> s.getOrder().getIdentifier().equals(order.getIdentifier())).findFirst();
        }

        public List<Shipment> findByStatus(ShipmentStatus status) {
            return store.values().stream().filter(s -> status.equals(s.getStatus())).toList();
        }
    }

    public static class Returns implements ReturnRequestRepositoryPort {
        public final Map<String, ReturnRequest> store = new LinkedHashMap<>();

        public ReturnRequest save(ReturnRequest request) {
            if (request.getIdentifier() == null) {
                request.setIdentifier(nextId("RET"));
            }
            store.put(request.getIdentifier(), request);
            return request;
        }

        public void update(ReturnRequest request) {
            store.put(request.getIdentifier(), request);
        }

        public Optional<ReturnRequest> findByIdentifier(ReturnRequest request) {
            return Optional.ofNullable(store.get(request.getIdentifier()));
        }

        public List<ReturnRequest> findByOrder(Order order) {
            return store.values().stream()
                    .filter(r -> r.getOrder().getIdentifier().equals(order.getIdentifier())).toList();
        }

        public List<ReturnRequest> findByBuyer(Buyer buyer) {
            return store.values().stream()
                    .filter(r -> r.getBuyer().getIdentification().equals(buyer.getIdentification())).toList();
        }

        public List<ReturnRequest> findByStatus(ReturnStatus status) {
            return store.values().stream().filter(r -> status.equals(r.getStatus())).toList();
        }

        public List<ReturnRequest> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    public static class Operations implements OperationRepositoryPort {
        public final List<Operation> store = new ArrayList<>();

        public Operation save(Operation operation) {
            operation.setOperationId((long) store.size() + 1);
            store.add(operation);
            return operation;
        }

        public List<Operation> findByUser(User user) {
            return store.stream().filter(o -> o.getPerformedBy() != null
                    && user.getUserId().equals(o.getPerformedBy().getUserId())).toList();
        }

        public List<Operation> findByAsset(MarketplaceAsset asset) {
            return store.stream().filter(o -> o.getAffectedAsset() != null
                    && asset.getIdentifier().equals(o.getAffectedAsset().getIdentifier())).toList();
        }

        public List<Operation> findAll() {
            return new ArrayList<>(store);
        }

        public long count(OperationType type) {
            return store.stream().filter(o -> type.equals(o.getOperationType())).count();
        }
    }

    public static class AuditLogs implements AuditLogRepositoryPort {
        public final List<AuditLog> store = new ArrayList<>();

        public AuditLog save(AuditLog auditLog) {
            auditLog.setAuditId("AUD-" + (store.size() + 1));
            store.add(auditLog);
            return auditLog;
        }

        public List<AuditLog> findByUser(User user) {
            return store.stream().filter(a -> a.getPerformedBy() != null
                    && user.getUserId().equals(a.getPerformedBy().getUserId())).toList();
        }

        public List<AuditLog> findByAsset(MarketplaceAsset asset) {
            return store.stream().filter(a -> a.getAffectedAsset() != null
                    && asset.getIdentifier().equals(a.getAffectedAsset().getIdentifier())).toList();
        }

        public List<AuditLog> findByOperationType(OperationType operationType) {
            return store.stream().filter(a -> operationType.equals(a.getOperationType())).toList();
        }

        public List<AuditLog> findAll() {
            return new ArrayList<>(store);
        }
    }

    public static class FakePasswords implements PasswordServicePort {
        public String encode(String rawPassword) {
            return "{enc}" + rawPassword;
        }

        public boolean matches(String rawPassword, String encodedPassword) {
            return encodedPassword != null && encodedPassword.equals("{enc}" + rawPassword);
        }
    }

    public static class FakeJwt implements JwtServicePort {
        public String generateToken(User user) {
            return "token-" + user.getUserId() + "-" + user.getAuthTokenVersion();
        }

        public boolean isValid(String token) {
            return token != null && token.startsWith("token-");
        }

        public User reconstructUser(String token) {
            User user = new User();
            user.setUserId(Integer.valueOf(token.split("-")[1]));
            return user;
        }
    }

    public static class FixedConfiguration implements BusinessConfigurationPort {
        public int paymentExpirationMinutes = 60;

        public BigDecimal getTaxRate() {
            return new BigDecimal("0.19");
        }

        public int getReturnWindowDays() {
            return 30;
        }

        public int getPaymentExpirationMinutes() {
            return paymentExpirationMinutes;
        }

        public int getLowStockThreshold() {
            return 5;
        }
    }
}
