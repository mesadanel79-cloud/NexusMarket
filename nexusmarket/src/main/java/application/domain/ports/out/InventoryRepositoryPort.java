package application.domain.ports.out;

import application.domain.models.InventoryItem;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Warehouse;
import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {

    InventoryItem save(InventoryItem item);

    void update(InventoryItem item);

    Optional<InventoryItem> findByIdentifier(InventoryItem item);

    List<InventoryItem> findByVariant(ProductVariant variant);

    List<InventoryItem> findByWarehouse(Warehouse warehouse);

    List<InventoryItem> findByProduct(Product product);

    List<InventoryItem> findAll();
}
