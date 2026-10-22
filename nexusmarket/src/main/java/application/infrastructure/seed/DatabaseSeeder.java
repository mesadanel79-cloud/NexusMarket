package application.infrastructure.seed;

import application.domain.models.Buyer;
import application.domain.models.InventoryItem;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Idempotent demo data so every role can be tested right after start-up.
 * All seeded users share the password {@code Password123*}.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private static final String PASSWORD = "Password123*";

    private final UserRepositoryPort userRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final boolean enabled;

    public DatabaseSeeder(UserRepositoryPort userRepositoryPort, BuyerRepositoryPort buyerRepositoryPort,
                          SellerRepositoryPort sellerRepositoryPort, WarehouseRepositoryPort warehouseRepositoryPort,
                          ProductRepositoryPort productRepositoryPort, InventoryRepositoryPort inventoryRepositoryPort,
                          PasswordServicePort passwordServicePort, @Value("${app.seed.enabled:true}") boolean enabled) {
        this.userRepositoryPort = userRepositoryPort;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.enabled = enabled;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        if (userRepositoryPort.existsByUsername(probe("admin.nexus"))) {
            log.info("Seed data already present: skipping.");
            return;
        }
        seedStaff("admin.nexus", UserRole.ADMIN, "A-0001", "Ana Admin", "admin@nexusmarket.com");
        seedStaff("supervisor.nexus", UserRole.SUPERVISOR, "S-0001", "Sergio Supervisor", "supervisor@nexusmarket.com");
        seedStaff("operator.nexus", UserRole.LOGISTIC_OPERATOR, "L-0001", "Olga Operator", "operator@nexusmarket.com");

        Warehouse marketplace = seedWarehouse("WH-MKT-01", "NexusMarket Medellin Hub", "Medellin",
                "Calle 30 #65-20", WarehouseType.MARKETPLACE, null);

        Seller seller = new Seller();
        seller.setIdentification("900111222");
        seller.setName("TecnoAndes S.A.S.");
        seller.setEmail("ventas@tecnoandes.com");
        seller.setPhoneNumber("6044440000");
        seller.setAddress("Carrera 43A #1-50 Medellin");
        seller.setStoreName("TecnoAndes Store");
        seller.register();
        sellerRepositoryPort.save(seller);
        seedProfileUser("seller.tecnoandes", UserRole.SELLER, seller, null, seller);
        Warehouse sellerWarehouse = seedWarehouse("WH-TEC-01", "TecnoAndes Bodega", "Envigado",
                "Calle 37 Sur #40-10", WarehouseType.SELLER, seller);

        Buyer buyer = new Buyer();
        buyer.setIdentification("1017000111");
        buyer.setName("Laura Gomez");
        buyer.setEmail("laura.gomez@mail.com");
        buyer.setPhoneNumber("3005550101");
        buyer.setAddress("Calle 10 #20-30 Medellin");
        buyer.setAdditionalAddresses(List.of("Carrera 70 #44-12 Medellin"));
        buyer.register();
        buyerRepositoryPort.save(buyer);
        seedProfileUser("buyer.laura", UserRole.BUYER, buyer, buyer, null);

        Product laptop = seedProduct("PRD-SEED-LAPTOP", seller, "Laptop Andes 14", "14 inch laptop for students.",
                ProductCategory.ELECTRONICS, ProductType.PHYSICAL,
                variant("LAPTOP-14-8GB", "8 GB RAM / 256 GB SSD", "2500000"),
                variant("LAPTOP-14-16GB", "16 GB RAM / 512 GB SSD", "3200000"));
        seedProduct("PRD-SEED-EBOOK", seller, "Hexagonal Architecture with Java", "Digital book (PDF).",
                ProductCategory.BOOKS, ProductType.DIGITAL, variant("EBOOK-HEX-JAVA", "PDF edition", "45000"));

        seedStock(marketplace, laptop, "LAPTOP-14-8GB", 10);
        seedStock(sellerWarehouse, laptop, "LAPTOP-14-8GB", 5);
        seedStock(sellerWarehouse, laptop, "LAPTOP-14-16GB", 3);

        log.info("Seed data created. Users: admin.nexus, supervisor.nexus, operator.nexus, seller.tecnoandes, "
                + "buyer.laura (password {}).", PASSWORD);
    }

    private void seedStaff(String username, UserRole role, String identification, String name, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(PASSWORD);
        user.setRole(role);
        user.setIdentification(identification);
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber("6045550000");
        user.setAddress("NexusMarket HQ Medellin");
        activateAndSave(user);
    }

    private void seedProfileUser(String username, UserRole role, application.domain.models.Person profile,
                                 Buyer buyer, Seller seller) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(PASSWORD);
        user.setRole(role);
        user.setIdentification(profile.getIdentification());
        user.setName(profile.getName());
        user.setEmail(profile.getEmail());
        user.setPhoneNumber(profile.getPhoneNumber());
        user.setAddress(profile.getAddress());
        user.setBuyer(buyer);
        user.setSeller(seller);
        activateAndSave(user);
    }

    private void activateAndSave(User user) {
        user.activateOnRegistration();
        user.setPassword(passwordServicePort.encode(PASSWORD));
        userRepositoryPort.save(user);
    }

    private Warehouse seedWarehouse(String code, String name, String city, String address, WarehouseType type,
                                    Seller owner) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(code);
        warehouse.setName(name);
        warehouse.setCity(city);
        warehouse.setAddress(address);
        warehouse.setWarehouseType(type);
        warehouse.setOwner(owner);
        warehouse.register();
        return warehouseRepositoryPort.save(warehouse);
    }

    private Product seedProduct(String identifier, Seller seller, String name, String description,
                                ProductCategory category, ProductType type, ProductVariant... variants) {
        Product product = new Product();
        product.setIdentifier(identifier);
        product.setSeller(seller);
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setProductType(type);
        product.setVariants(new java.util.ArrayList<>(List.of(variants)));
        product.publish();
        return productRepositoryPort.save(product);
    }

    private void seedStock(Warehouse warehouse, Product product, String sku, int quantity) {
        InventoryItem item = InventoryItem.open(warehouse, product, product.requireVariant(sku));
        item.registerInflow(quantity);
        inventoryRepositoryPort.save(item);
    }

    private static ProductVariant variant(String sku, String description, String price) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(sku);
        variant.setDescription(description);
        variant.setPrice(Money.of(new BigDecimal(price), Currency.COP));
        return variant;
    }

    private static User probe(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}
