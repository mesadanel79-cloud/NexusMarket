package application.domain.ports.out;

import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.User;
import java.util.List;

public interface OperationRepositoryPort {

    Operation save(Operation operation);

    List<Operation> findByUser(User user);

    List<Operation> findByAsset(MarketplaceAsset asset);

    List<Operation> findAll();
}
