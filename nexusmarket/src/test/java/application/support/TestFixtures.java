package application.support;

import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Builders of valid domain objects and a ready-to-use marketplace scenario.
 */
public final class TestFixtures {

    public static final String PASSWORD = "Password123*";

    public final ServiceHarness h;
    public User admin;
    public User supervisor;
    public User operator;
    public User seller;
    public User otherSeller;
    public User buyer;
    public Warehouse marketplaceWarehouse;
    public Product laptop;
    public Product ebook;

    public TestFixtures(ServiceHarness harness) {
        this.h = harness;
    }

    /** Admin, staff, two sellers (with their first warehouses), one buyer and two products. */
    public TestFixtures scenario() {
        admin = staff("admin.test", UserRole.ADMIN, "A-1");
        User adminUser = admin;
        supervisor = h.registerStaff.register(adminUser, newStaff("supervisor.test", UserRole.SUPERVISOR, "S-1"));
        operator = h.registerStaff.register(adminUser, newStaff("operator.test", UserRole.LOGISTIC_OPERATOR, "L-1"));
        seller = h.registerSeller.register(admin, newSellerUser("seller.test", "900100100", "store@test.com"),
                warehouse("WH-SEL-01", WarehouseType.SELLER));
        otherSeller = h.registerSeller.register(admin, newSellerUser("seller.other", "900200200", "other@test.com"),
                warehouse("WH-OTH-01", WarehouseType.SELLER));
        marketplaceWarehouse = h.registerWarehouse.register(admin, warehouse("WH-MKT-01", WarehouseType.MARKETPLACE));
        buyer = h.registerBuyer.register(newBuyerUser("buyer.test", "1017000001", "buyer@test.com"));
        laptop = h.createProduct.create(seller, product("Laptop", ProductType.PHYSICAL,
                variant("LAP-8GB", "1000000"), variant("LAP-16GB", "1500000")));
        ebook = h.createProduct.create(seller, product("Ebook", ProductType.DIGITAL, variant("EBOOK-1", "50000")));
        return this;
    }

    /** Creates the first ADMIN directly (as the seeder does). */
    public User staff(String username, UserRole role, String identification) {
        User user = newStaff(username, role, identification);
        user.activateOnRegistration();
        user.setPassword(h.passwords.encode(PASSWORD));
        return h.users.save(user);
    }

    public static User newStaff(String username, UserRole role, String identification) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(PASSWORD);
        user.setRole(role);
        user.setIdentification(identification);
        user.setName("Staff " + username);
        user.setEmail(username + "@nexusmarket.com");
        user.setPhoneNumber("3001234567");
        user.setAddress("HQ");
        return user;
    }

    public static User newBuyerUser(String username, String identification, String email) {
        Buyer buyer = new Buyer();
        buyer.setIdentification(identification);
        buyer.setName("Buyer " + username);
        buyer.setEmail(email);
        buyer.setPhoneNumber("3009876543");
        buyer.setAddress("Main street 1");
        buyer.setAdditionalAddresses(new ArrayList<>(List.of("Office street 2")));
        User user = new User();
        user.setUsername(username);
        user.setPassword(PASSWORD);
        user.setBuyer(buyer);
        return user;
    }

    public static User newSellerUser(String username, String identification, String email) {
        application.domain.models.Seller seller = new application.domain.models.Seller();
        seller.setIdentification(identification);
        seller.setName("Seller " + username);
        seller.setEmail(email);
        seller.setPhoneNumber("6041112222");
        seller.setAddress("Industrial zone");
        seller.setStoreName("Store " + username);
        User user = new User();
        user.setUsername(username);
        user.setPassword(PASSWORD);
        user.setSeller(seller);
        return user;
    }

    public static Warehouse warehouse(String code, WarehouseType type) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(code);
        warehouse.setName("Warehouse " + code);
        warehouse.setCity("Medellin");
        warehouse.setAddress("Street " + code);
        warehouse.setWarehouseType(type);
        return warehouse;
    }

    public static ProductVariant variant(String sku, String price) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(sku);
        variant.setDescription("Variant " + sku);
        variant.setPrice(Money.of(new BigDecimal(price), Currency.COP));
        return variant;
    }

    public static Product product(String name, ProductType type, ProductVariant... variants) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(name + " description");
        product.setCategory(type == ProductType.DIGITAL ? ProductCategory.BOOKS : ProductCategory.ELECTRONICS);
        product.setProductType(type);
        product.setVariants(new ArrayList<>(List.of(variants)));
        return product;
    }

    public static Money cop(String amount) {
        return Money.of(new BigDecimal(amount), Currency.COP);
    }

    public static Warehouse warehouseRef(String code) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(code);
        return warehouse;
    }

    public static ProductVariant skuRef(String sku) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(sku);
        return variant;
    }
}
