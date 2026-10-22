package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ReturnRequestJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataReturnRequestRepository extends JpaRepository<ReturnRequestJpaEntity, String> {

    List<ReturnRequestJpaEntity> findByOrderIdentifier(String orderIdentifier);

    List<ReturnRequestJpaEntity> findByBuyerIdentificationOrderByRequestedAtDesc(String buyerIdentification);

    List<ReturnRequestJpaEntity> findByStatusOrderByRequestedAtAsc(String status);
}
