package application.domain.ports.out;

import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import java.util.List;

public interface InventoryMovementRepositoryPort {

    InventoryMovement save(InventoryMovement movement);

    List<InventoryMovement> findByInventoryItem(InventoryItem item);
}
