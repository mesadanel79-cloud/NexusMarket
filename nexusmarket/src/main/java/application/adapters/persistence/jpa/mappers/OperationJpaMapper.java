package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;

import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;

public final class OperationJpaMapper {

    private OperationJpaMapper() {
    }

    public static OperationJpaEntity toEntity(Operation domain) {
        OperationJpaEntity entity = new OperationJpaEntity();
        entity.setOperationId(domain.getOperationId());
        entity.setOperationType(code(domain.getOperationType()));
        entity.setExecutionDate(domain.getExecutionDate());
        User user = domain.getPerformedBy();
        if (user != null) {
            entity.setPerformedByUserId(user.getUserId());
            entity.setPerformedByUsername(user.getUsername());
            entity.setPerformedByRole(code(user.getRole()));
        }
        MarketplaceAsset asset = domain.getAffectedAsset();
        if (asset != null) {
            entity.setAffectedAssetIdentifier(asset.getIdentifier());
            entity.setAffectedAssetType(AssetReferences.typeOf(asset));
        }
        return entity;
    }

    public static Operation toDomain(OperationJpaEntity entity) {
        Operation domain = new Operation();
        domain.setOperationId(entity.getOperationId());
        domain.setOperationType(OperationType.fromCode(entity.getOperationType()));
        domain.setExecutionDate(entity.getExecutionDate());
        if (entity.getPerformedByUserId() != null) {
            User user = new User();
            user.setUserId(entity.getPerformedByUserId());
            user.setUsername(entity.getPerformedByUsername());
            user.setRole(UserRole.fromCode(entity.getPerformedByRole()));
            domain.setPerformedBy(user);
        }
        domain.setAffectedAsset(AssetReferences.reference(entity.getAffectedAssetType(), entity.getAffectedAssetIdentifier()));
        return domain;
    }
}
