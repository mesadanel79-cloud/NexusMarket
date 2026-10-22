package application.domain.ports.out;

import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.valueobjects.ShipmentStatus;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepositoryPort {

    Shipment save(Shipment shipment);

    void update(Shipment shipment);

    Optional<Shipment> findByIdentifier(Shipment shipment);

    Optional<Shipment> findByOrder(Order order);

    List<Shipment> findByStatus(ShipmentStatus status);
}
