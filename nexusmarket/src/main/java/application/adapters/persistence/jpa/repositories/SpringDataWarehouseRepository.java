package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWarehouseRepository extends JpaRepository<WarehouseJpaEntity, String> {
}
