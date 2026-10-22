package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOperationRepository extends JpaRepository<OperationJpaEntity, Long> {

    List<OperationJpaEntity> findByPerformedByUserIdOrderByExecutionDateDesc(Integer performedByUserId);

    List<OperationJpaEntity> findByAffectedAssetIdentifierOrderByExecutionDateDesc(String affectedAssetIdentifier);

    List<OperationJpaEntity> findAllByOrderByExecutionDateDesc();
}
