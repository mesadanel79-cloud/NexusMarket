package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.mappers.BuyerJpaMapper;
import application.adapters.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataBuyerRepository;
import application.adapters.persistence.jpa.repositories.SpringDataOrderRepository;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Seller;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OrderJpaAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository orderRepository;
    private final SpringDataBuyerRepository buyerRepository;

    public OrderJpaAdapter(SpringDataOrderRepository orderRepository, SpringDataBuyerRepository buyerRepository) {
        this.orderRepository = orderRepository;
        this.buyerRepository = buyerRepository;
    }

    @Override
    public Order save(Order order) {
        if (order.getIdentifier() == null || order.getIdentifier().isBlank()) {
            order.setIdentifier(IdentifierGenerator.next("ORD"));
        }
        orderRepository.save(OrderJpaMapper.toEntity(order));
        return order;
    }

    @Override
    public void update(Order order) {
        orderRepository.save(OrderJpaMapper.toEntity(order));
    }

    @Override
    public Optional<Order> findByIdentifier(Order order) {
        if (order == null || order.getIdentifier() == null) {
            return Optional.empty();
        }
        return orderRepository.findById(order.getIdentifier()).map(this::toDomain);
    }

    @Override
    public List<Order> findByBuyer(Buyer buyer) {
        return orderRepository.findByBuyerIdentificationOrderByCreatedAtDesc(buyer.getIdentification()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Order> findBySeller(Seller seller) {
        return orderRepository.findDistinctByLinesSellerIdentificationOrderByCreatedAtDesc(seller.getIdentification())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtAsc(status.getCode()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    public Order toDomain(OrderJpaEntity entity) {
        Buyer buyer = buyerRepository.findById(entity.getBuyerIdentification())
                .map(BuyerJpaMapper::toDomain).orElseGet(() -> {
                    Buyer reference = new Buyer();
                    reference.setIdentification(entity.getBuyerIdentification());
                    return reference;
                });
        return OrderJpaMapper.toDomain(entity, buyer);
    }
}
