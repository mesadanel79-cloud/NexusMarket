package application.domain.ports.in;

import application.domain.models.InventoryItem;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Money;
import java.util.List;

/**
 * Use cases of the SELLER role.
 */
public interface SellerPort {

    Seller consultMyStore(User user);

    void changeMyPassword(User user, String currentPassword, String newPassword);

    Product createProduct(User user, Product product);

    List<Product> consultMyProducts(User user);

    Product addVariant(User user, Product product, ProductVariant variant);

    Product changeVariantPrice(User user, Product product, String sku, Money newPrice);

    Product suspendProduct(User user, Product product);

    Product reactivateProduct(User user, Product product);

    Product discontinueProduct(User user, Product product);

    List<InventoryItem> consultProductInventory(User user, Product product);

    InventoryItem registerInventoryInflow(User user, Warehouse warehouse, ProductVariant variant, int quantity);

    InventoryItem adjustInventory(User user, InventoryItem item, int newAvailableQuantity, String reason);

    InventoryItem markDamagedInventory(User user, InventoryItem item, int quantity, String reason);

    List<Order> consultMySales(User user);
}
