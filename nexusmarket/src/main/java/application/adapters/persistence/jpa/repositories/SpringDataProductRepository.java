package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, String> {

    Optional<ProductJpaEntity> findByVariantsSku(String sku);

    List<ProductJpaEntity> findBySellerIdentification(String sellerIdentification);

    List<ProductJpaEntity> findByStatus(String status);

    List<ProductJpaEntity> findByStatusAndCategory(String status, String category);
}
