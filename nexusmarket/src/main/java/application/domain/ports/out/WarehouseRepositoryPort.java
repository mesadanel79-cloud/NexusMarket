package application.domain.ports.out;

import application.domain.models.Warehouse;
import java.util.List;
import java.util.Optional;

public interface WarehouseRepositoryPort {

    Warehouse save(Warehouse warehouse);

    void update(Warehouse warehouse);

    Optional<Warehouse> findByIdentifier(Warehouse warehouse);

    boolean existsByIdentifier(Warehouse warehouse);

    List<Warehouse> findAll();
}
