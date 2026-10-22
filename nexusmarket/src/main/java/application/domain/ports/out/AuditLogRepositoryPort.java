package application.domain.ports.out;

import application.domain.models.AuditLog;
import application.domain.models.MarketplaceAsset;
import application.domain.models.User;
import application.domain.valueobjects.OperationType;
import java.util.List;

public interface AuditLogRepositoryPort {

    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByUser(User user);

    List<AuditLog> findByAsset(MarketplaceAsset asset);

    List<AuditLog> findByOperationType(OperationType operationType);

    List<AuditLog> findAll();
}
