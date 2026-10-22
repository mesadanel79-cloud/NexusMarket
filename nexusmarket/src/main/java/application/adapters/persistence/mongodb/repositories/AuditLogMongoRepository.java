package application.adapters.persistence.mongodb.repositories;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogMongoRepository extends MongoRepository<AuditLogDocument, String> {

    List<AuditLogDocument> findByPerformedByUserIdOrderByOperationDateDesc(Integer performedByUserId);

    List<AuditLogDocument> findByAffectedAssetIdentifierOrderByOperationDateDesc(String affectedAssetIdentifier);

    List<AuditLogDocument> findByOperationTypeOrderByOperationDateDesc(String operationType);

    List<AuditLogDocument> findAllByOrderByOperationDateDesc();
}
