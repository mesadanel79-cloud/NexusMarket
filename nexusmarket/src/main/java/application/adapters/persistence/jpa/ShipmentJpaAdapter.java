package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.adapters.persistence.jpa.mappers.ShipmentJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataOrderRepository;
import application.adapters.persistence.jpa.repositories.SpringDataShipmentRepository;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.valueobjects.ShipmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ShipmentJpaAdapter implements ShipmentRepositoryPort {

    private final SpringDataShipmentRepository shipmentRepository;
    private final SpringDataOrderRepository orderRepository;
    private final OrderJpaAdapter orderJpaAdapter;

    public ShipmentJpaAdapter(SpringDataShipmentRepository shipmentRepository, SpringDataOrderRepository orderRepository,
                              OrderJpaAdapter orderJpaAdapter) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.orderJpaAdapter = orderJpaAdapter;
    }

    @Override
    public Shipment save(Shipment shipment) {
        if (shipment.getIdentifier() == null || shipment.getIdentifier().isBlank()) {
            shipment.setIdentifier(IdentifierGenerator.next("SHP"));
        }
        shipmentRepository.save(ShipmentJpaMapper.toEntity(shipment));
        return shipment;
    }

    @Override
    public void update(Shipment shipment) {
        shipmentRepository.save(ShipmentJpaMapper.toEntity(shipment));
    }

    @Override
    public Optional<Shipment> findByIdentifier(Shipment shipment) {
        if (shipment == null || shipment.getIdentifier() == null) {
            return Optional.empty();
        }
        return shipmentRepository.findById(shipment.getIdentifier()).map(this::toDomain);
    }

    @Override
    public Optional<Shipment> findByOrder(Order order) {
        return shipmentRepository.findByOrderIdentifier(order.getIdentifier()).map(this::toDomain);
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status.getCode()).stream().map(this::toDomain).toList();
    }

    private Shipment toDomain(ShipmentJpaEntity entity) {
        Order order = orderRepository.findById(entity.getOrderIdentifier()).map(orderJpaAdapter::toDomain)
                .orElseGet(() -> {
                    Order reference = new Order();
                    reference.setIdentifier(entity.getOrderIdentifier());
                    return reference;
                });
        return ShipmentJpaMapper.toDomain(entity, order);
    }
}
