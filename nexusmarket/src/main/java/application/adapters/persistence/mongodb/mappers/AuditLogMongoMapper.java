package application.adapters.persistence.mongodb.mappers;

import application.adapters.persistence.jpa.mappers.AssetReferences;
import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;
import java.util.LinkedHashMap;

public final class AuditLogMongoMapper {

    private AuditLogMongoMapper() {
    }

    public static AuditLogDocument toDocument(AuditLog domain) {
        AuditLogDocument document = new AuditLogDocument();
        document.setAuditId(domain.getAuditId());
        document.setOperationType(domain.getOperationType().getCode());
        document.setOperationDate(domain.getOperationDate());
        if (domain.getPerformedBy() != null) {
            document.setPerformedByUserId(domain.getPerformedBy().getUserId());
            document.setPerformedByUsername(domain.getPerformedBy().getUsername());
        }
        document.setUserRole(domain.getUserRole() == null ? null : domain.getUserRole().getCode());
        if (domain.getAffectedAsset() != null) {
            document.setAffectedAssetIdentifier(domain.getAffectedAsset().getIdentifier());
            document.setAffectedAssetType(AssetReferences.typeOf(domain.getAffectedAsset()));
        }
        document.setDetails(domain.getDetails() == null ? new LinkedHashMap<>() : new LinkedHashMap<>(domain.getDetails()));
        return document;
    }

    public static AuditLog toDomain(AuditLogDocument document) {
        AuditLog domain = new AuditLog();
        domain.setAuditId(document.getAuditId());
        domain.setOperationType(OperationType.fromCode(document.getOperationType()));
        domain.setOperationDate(document.getOperationDate());
        if (document.getPerformedByUserId() != null) {
            User user = new User();
            user.setUserId(document.getPerformedByUserId());
            user.setUsername(document.getPerformedByUsername());
            user.setRole(UserRole.fromCode(document.getUserRole()));
            domain.setPerformedBy(user);
        }
        domain.setUserRole(UserRole.fromCode(document.getUserRole()));
        domain.setAffectedAsset(AssetReferences.reference(document.getAffectedAssetType(),
                document.getAffectedAssetIdentifier()));
        domain.setDetails(document.getDetails());
        return domain;
    }
}
