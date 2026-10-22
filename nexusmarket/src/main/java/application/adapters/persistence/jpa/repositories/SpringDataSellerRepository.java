package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSellerRepository extends JpaRepository<SellerJpaEntity, String> {

    boolean existsByEmailIgnoreCase(String email);
}
