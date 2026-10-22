package application.adapters.useCases;

import application.domain.models.InventoryItem;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.SellerPort;
import application.domain.services.catalog.ChangeProductStatusService;
import application.domain.services.catalog.ConsultCatalogService;
import application.domain.services.catalog.CreateProductService;
import application.domain.services.catalog.ManageProductVariantsService;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.RegisterInventoryInflowService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.seller.ConsultSellersService;
import application.domain.services.user.ChangePasswordService;
import application.domain.valueobjects.Money;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerUseCaseImpl implements SellerPort {

    private final ConsultSellersService consultSellersService;
    private final ChangePasswordService changePasswordService;
    private final CreateProductService createProductService;
    private final ConsultCatalogService consultCatalogService;
    private final ManageProductVariantsService manageProductVariantsService;
    private final ChangeProductStatusService changeProductStatusService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultOrdersService consultOrdersService;
    private final RegisterInventoryInflowService registerInventoryInflowService;
    private final AdjustInventoryService adjustInventoryService;

    public SellerUseCaseImpl(ConsultSellersService consultSellersService, ChangePasswordService changePasswordService,
                             CreateProductService createProductService, ConsultCatalogService consultCatalogService,
                             ManageProductVariantsService manageProductVariantsService,
                             ChangeProductStatusService changeProductStatusService,
                             ConsultInventoryService consultInventoryService,
                             ConsultOrdersService consultOrdersService,
                             RegisterInventoryInflowService registerInventoryInflowService,
                             AdjustInventoryService adjustInventoryService) {
        this.consultSellersService = consultSellersService;
        this.changePasswordService = changePasswordService;
        this.createProductService = createProductService;
        this.consultCatalogService = consultCatalogService;
        this.manageProductVariantsService = manageProductVariantsService;
        this.changeProductStatusService = changeProductStatusService;
        this.consultInventoryService = consultInventoryService;
        this.consultOrdersService = consultOrdersService;
        this.registerInventoryInflowService = registerInventoryInflowService;
        this.adjustInventoryService = adjustInventoryService;
    }

    @Override
    @Transactional(readOnly = true)
    public Seller consultMyStore(User user) {
        return consultSellersService.consultMyStore(user);
    }

    @Override
    @Transactional
    public void changeMyPassword(User user, String currentPassword, String newPassword) {
        changePasswordService.changePassword(user, currentPassword, newPassword);
    }

    @Override
    @Transactional
    public Product createProduct(User user, Product product) {
        return createProductService.create(user, product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> consultMyProducts(User user) {
        return consultCatalogService.consultSellerProducts(user);
    }

    @Override
    @Transactional
    public Product addVariant(User user, Product product, ProductVariant variant) {
        return manageProductVariantsService.addVariant(user, product, variant);
    }

    @Override
    @Transactional
    public Product changeVariantPrice(User user, Product product, String sku, Money newPrice) {
        return manageProductVariantsService.changeVariantPrice(user, product, sku, newPrice);
    }

    @Override
    @Transactional
    public Product suspendProduct(User user, Product product) {
        return changeProductStatusService.suspend(user, product);
    }

    @Override
    @Transactional
    public Product reactivateProduct(User user, Product product) {
        return changeProductStatusService.reactivate(user, product);
    }

    @Override
    @Transactional
    public Product discontinueProduct(User user, Product product) {
        return changeProductStatusService.discontinue(user, product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItem> consultProductInventory(User user, Product product) {
        return consultInventoryService.consultByProduct(user, product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> consultMySales(User user) {
        return consultOrdersService.consultSellerSales(user);
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
}
