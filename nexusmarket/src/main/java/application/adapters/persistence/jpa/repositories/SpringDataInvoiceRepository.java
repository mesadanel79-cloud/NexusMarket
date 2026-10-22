package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceJpaEntity, String> {

    Optional<InvoiceJpaEntity> findByOrderIdentifier(String orderIdentifier);

    List<InvoiceJpaEntity> findByBuyerIdentificationOrderByIssueDateDesc(String buyerIdentification);
}
