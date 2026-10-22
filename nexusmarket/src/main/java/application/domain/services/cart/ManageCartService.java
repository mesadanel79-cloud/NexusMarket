package application.domain.services.cart;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidCartException;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.inventory.InventoryAllocationService;

/**
 * Adds, updates and removes items of the authenticated buyer's cart. For physical
 * products the requested quantity is checked against the stock available in active
 * warehouses (the stock is only reserved at checkout).
 */
public class ManageCartService {

    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final InventoryAllocationService inventoryAllocationService;

    public ManageCartService(CartRepositoryPort cartRepositoryPort, ProductRepositoryPort productRepositoryPort,
                             ValidateOwnershipService validateOwnershipService,
                             InventoryAllocationService inventoryAllocationService) {
        this.cartRepositoryPort = cartRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.inventoryAllocationService = inventoryAllocationService;
    }

    public Cart addItem(User user, CartItem item) {
        Buyer buyer = requirePurchasingBuyer(user);
        String sku = requireSku(item);
        Product product = loadProduct(sku);
        Cart cart = loadOrOpen(buyer);
        int alreadyInCart = cart.findItem(sku).map(CartItem::getQuantity).orElse(0);
        cart.addItem(product, sku, item.getQuantity());
        if (product.isPhysical()) {
            checkStock(product.requireVariant(sku), alreadyInCart + item.getQuantity());
        }
        return cartRepositoryPort.save(cart);
    }

    public Cart updateItem(User user, CartItem item) {
        Buyer buyer = requirePurchasingBuyer(user);
        String sku = requireSku(item);
        Cart cart = loadOrOpen(buyer);
        cart.updateQuantity(sku, item.getQuantity());
        CartItem updated = cart.findItem(sku).orElseThrow();
        if (updated.getProduct() != null && updated.getProduct().isPhysical()) {
            checkStock(updated.getVariant(), item.getQuantity());
        }
        return cartRepositoryPort.save(cart);
    }

    public Cart removeItem(User user, CartItem item) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        Cart cart = loadOrOpen(buyer);
        cart.removeItem(requireSku(item));
        return cartRepositoryPort.save(cart);
    }

    private Buyer requirePurchasingBuyer(User user) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        if (!buyer.canPurchase()) {
            throw new InvalidCartException("Suspended buyers cannot add products to the cart.");
        }
        return buyer;
    }

    private void checkStock(ProductVariant variant, int requested) {
        int available = inventoryAllocationService.availableQuantity(variant);
        if (available < requested) {
            throw new InsufficientStockException("Only " + available + " units of SKU " + variant.getSku()
                    + " are available.");
        }
    }

    private Product loadProduct(String sku) {
        ProductVariant probe = new ProductVariant();
        probe.setSku(sku);
        return productRepositoryPort.findByVariantSku(probe)
                .orElseThrow(() -> new EntityNotFoundException("Product variant", sku));
    }

    private Cart loadOrOpen(Buyer buyer) {
        return cartRepositoryPort.findByBuyer(buyer).orElseGet(() -> Cart.openFor(buyer));
    }

    private static String requireSku(CartItem item) {
        if (item == null || item.getSku() == null || item.getSku().isBlank()) {
            throw new InvalidCartException("SKU is required.");
        }
        return item.getSku();
    }
}
