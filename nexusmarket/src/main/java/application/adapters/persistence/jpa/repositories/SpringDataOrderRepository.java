package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, String> {

    List<OrderJpaEntity> findByBuyerIdentificationOrderByCreatedAtDesc(String buyerIdentification);

    List<OrderJpaEntity> findDistinctByLinesSellerIdentificationOrderByCreatedAtDesc(String sellerIdentification);

    List<OrderJpaEntity> findByStatusOrderByCreatedAtAsc(String status);

    List<OrderJpaEntity> findAllByOrderByCreatedAtDesc();
}
