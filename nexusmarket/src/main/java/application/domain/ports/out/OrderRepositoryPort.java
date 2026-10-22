package application.domain.ports.out;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Seller;
import application.domain.valueobjects.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    void update(Order order);

    Optional<Order> findByIdentifier(Order order);

    List<Order> findByBuyer(Buyer buyer);

    List<Order> findBySeller(Seller seller);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();
}
