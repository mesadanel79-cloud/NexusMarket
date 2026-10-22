package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import application.adapters.persistence.jpa.mappers.OperationJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataOperationRepository;
import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.OperationRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OperationJpaAdapter implements OperationRepositoryPort {

    private final SpringDataOperationRepository operationRepository;

    public OperationJpaAdapter(SpringDataOperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public Operation save(Operation operation) {
        OperationJpaEntity saved = operationRepository.save(OperationJpaMapper.toEntity(operation));
        operation.setOperationId(saved.getOperationId());
        return operation;
    }

    @Override
    public List<Operation> findByUser(User user) {
        return operationRepository.findByPerformedByUserIdOrderByExecutionDateDesc(user.getUserId()).stream()
                .map(OperationJpaMapper::toDomain).toList();
    }

    @Override
    public List<Operation> findByAsset(MarketplaceAsset asset) {
        return operationRepository.findByAffectedAssetIdentifierOrderByExecutionDateDesc(asset.getIdentifier()).stream()
                .map(OperationJpaMapper::toDomain).toList();
    }

    @Override
    public List<Operation> findAll() {
        return operationRepository.findAllByOrderByExecutionDateDesc().stream()
                .map(OperationJpaMapper::toDomain).toList();
    }
}
