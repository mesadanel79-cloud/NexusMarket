package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.adapters.persistence.jpa.mappers.SellerJpaMapper;
import application.adapters.persistence.jpa.mappers.WarehouseJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataSellerRepository;
import application.adapters.persistence.jpa.repositories.SpringDataWarehouseRepository;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class WarehouseJpaAdapter implements WarehouseRepositoryPort {

    private final SpringDataWarehouseRepository warehouseRepository;
    private final SpringDataSellerRepository sellerRepository;

    public WarehouseJpaAdapter(SpringDataWarehouseRepository warehouseRepository,
                               SpringDataSellerRepository sellerRepository) {
        this.warehouseRepository = warehouseRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        warehouseRepository.save(WarehouseJpaMapper.toEntity(warehouse));
        return warehouse;
    }

    @Override
    public void update(Warehouse warehouse) {
        warehouseRepository.save(WarehouseJpaMapper.toEntity(warehouse));
    }

    @Override
    public Optional<Warehouse> findByIdentifier(Warehouse warehouse) {
        if (warehouse == null || warehouse.getIdentifier() == null) {
            return Optional.empty();
        }
        return warehouseRepository.findById(warehouse.getIdentifier()).map(this::toDomain);
    }

    @Override
    public boolean existsByIdentifier(Warehouse warehouse) {
        return warehouse.getIdentifier() != null && warehouseRepository.existsById(warehouse.getIdentifier());
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll().stream().map(this::toDomain).toList();
    }

    public Warehouse toDomain(WarehouseJpaEntity entity) {
        Seller owner = entity.getOwnerSellerIdentification() == null ? null
                : sellerRepository.findById(entity.getOwnerSellerIdentification())
                        .map(SellerJpaMapper::toDomain).orElse(null);
        return WarehouseJpaMapper.toDomain(entity, owner);
    }
}
