package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ProductVariantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductVariantRepository extends JpaRepository<ProductVariantJpaEntity, String> {
}
