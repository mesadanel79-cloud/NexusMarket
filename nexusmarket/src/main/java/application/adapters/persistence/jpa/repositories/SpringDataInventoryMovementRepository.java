package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInventoryMovementRepository extends JpaRepository<InventoryMovementJpaEntity, Long> {

    List<InventoryMovementJpaEntity> findByInventoryItemIdentifierOrderByOccurredAtDesc(String inventoryItemIdentifier);
}
