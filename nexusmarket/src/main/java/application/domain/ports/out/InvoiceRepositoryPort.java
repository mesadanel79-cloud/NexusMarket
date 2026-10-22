package application.domain.ports.out;

import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.Order;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {

    Invoice save(Invoice invoice);

    void update(Invoice invoice);

    Optional<Invoice> findByIdentifier(Invoice invoice);

    Optional<Invoice> findByOrder(Order order);

    List<Invoice> findByBuyer(Buyer buyer);

    List<Invoice> findAll();
}
