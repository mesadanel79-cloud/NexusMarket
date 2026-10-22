package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InventoryItemJpaEntity;
import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.adapters.persistence.jpa.mappers.InventoryJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataInventoryItemRepository;
import application.adapters.persistence.jpa.repositories.SpringDataInventoryMovementRepository;
import application.adapters.persistence.jpa.repositories.SpringDataProductRepository;
import application.adapters.persistence.jpa.repositories.SpringDataWarehouseRepository;
import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Persistence of the distributed inventory (stock records and their movements).
 */
@Repository
public class InventoryJpaAdapter implements InventoryRepositoryPort, InventoryMovementRepositoryPort {

    private final SpringDataInventoryItemRepository itemRepository;
    private final SpringDataInventoryMovementRepository movementRepository;
    private final SpringDataWarehouseRepository warehouseRepository;
    private final SpringDataProductRepository productRepository;
    private final WarehouseJpaAdapter warehouseJpaAdapter;
    private final ProductJpaAdapter productJpaAdapter;

    public InventoryJpaAdapter(SpringDataInventoryItemRepository itemRepository,
                               SpringDataInventoryMovementRepository movementRepository,
                               SpringDataWarehouseRepository warehouseRepository,
                               SpringDataProductRepository productRepository,
                               WarehouseJpaAdapter warehouseJpaAdapter, ProductJpaAdapter productJpaAdapter) {
        this.itemRepository = itemRepository;
        this.movementRepository = movementRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.warehouseJpaAdapter = warehouseJpaAdapter;
        this.productJpaAdapter = productJpaAdapter;
    }

    @Override
    public InventoryItem save(InventoryItem item) {
        itemRepository.save(InventoryJpaMapper.toEntity(item));
        return item;
    }

    @Override
    public void update(InventoryItem item) {
        itemRepository.save(InventoryJpaMapper.toEntity(item));
    }

    @Override
    public Optional<InventoryItem> findByIdentifier(InventoryItem item) {
        if (item == null || item.getIdentifier() == null) {
            return Optional.empty();
        }
        return itemRepository.findById(item.getIdentifier()).map(this::toDomain);
    }

    @Override
    public List<InventoryItem> findByVariant(ProductVariant variant) {
        return itemRepository.findBySku(variant.getSku()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<InventoryItem> findByWarehouse(Warehouse warehouse) {
        return itemRepository.findByWarehouseIdentifier(warehouse.getIdentifier()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<InventoryItem> findByProduct(Product product) {
        return itemRepository.findByProductIdentifier(product.getIdentifier()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<InventoryItem> findAll() {
        return itemRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        InventoryMovementJpaEntity saved = movementRepository.save(InventoryJpaMapper.toEntity(movement));
        movement.setMovementId(saved.getMovementId());
        return movement;
    }

    @Override
    public List<InventoryMovement> findByInventoryItem(InventoryItem item) {
        return movementRepository.findByInventoryItemIdentifierOrderByOccurredAtDesc(item.getIdentifier()).stream()
                .map(entity -> InventoryJpaMapper.toDomain(entity, item))
                .toList();
    }

    private InventoryItem toDomain(InventoryItemJpaEntity entity) {
        Warehouse warehouse = warehouseRepository.findById(entity.getWarehouseIdentifier())
                .map(warehouseJpaAdapter::toDomain).orElse(null);
        Product product = productRepository.findById(entity.getProductIdentifier())
                .map(productJpaAdapter::toDomain).orElse(null);
        return InventoryJpaMapper.toDomain(entity, warehouse, product);
    }
}
