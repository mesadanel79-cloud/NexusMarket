package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryItemJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInventoryItemRepository extends JpaRepository<InventoryItemJpaEntity, String> {

    List<InventoryItemJpaEntity> findBySku(String sku);

    List<InventoryItemJpaEntity> findByWarehouseIdentifier(String warehouseIdentifier);

    List<InventoryItemJpaEntity> findByProductIdentifier(String productIdentifier);
}
