package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.adapters.persistence.jpa.mappers.InvoiceJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataInvoiceRepository;
import application.adapters.persistence.jpa.repositories.SpringDataOrderRepository;
import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.ports.out.InvoiceRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceJpaAdapter implements InvoiceRepositoryPort {

    private final SpringDataInvoiceRepository invoiceRepository;
    private final SpringDataOrderRepository orderRepository;
    private final OrderJpaAdapter orderJpaAdapter;

    public InvoiceJpaAdapter(SpringDataInvoiceRepository invoiceRepository, SpringDataOrderRepository orderRepository,
                             OrderJpaAdapter orderJpaAdapter) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
        this.orderJpaAdapter = orderJpaAdapter;
    }

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice.getIdentifier() == null || invoice.getIdentifier().isBlank()) {
            invoice.setIdentifier(IdentifierGenerator.next("INVC"));
        }
        invoiceRepository.save(InvoiceJpaMapper.toEntity(invoice));
        return invoice;
    }

    @Override
    public void update(Invoice invoice) {
        invoiceRepository.save(InvoiceJpaMapper.toEntity(invoice));
    }

    @Override
    public Optional<Invoice> findByIdentifier(Invoice invoice) {
        if (invoice == null || invoice.getIdentifier() == null) {
            return Optional.empty();
        }
        return invoiceRepository.findById(invoice.getIdentifier()).map(this::toDomain);
    }

    @Override
    public Optional<Invoice> findByOrder(Order order) {
        return invoiceRepository.findByOrderIdentifier(order.getIdentifier()).map(this::toDomain);
    }

    @Override
    public List<Invoice> findByBuyer(Buyer buyer) {
        return invoiceRepository.findByBuyerIdentificationOrderByIssueDateDesc(buyer.getIdentification()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll().stream().map(this::toDomain).toList();
    }

    private Invoice toDomain(InvoiceJpaEntity entity) {
        Order order = orderRepository.findById(entity.getOrderIdentifier()).map(orderJpaAdapter::toDomain).orElse(null);
        Buyer buyer = order != null ? order.getBuyer() : null;
        return InvoiceJpaMapper.toDomain(entity, order, buyer);
    }
}
