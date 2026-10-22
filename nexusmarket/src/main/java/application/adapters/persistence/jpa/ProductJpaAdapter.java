package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.mappers.ProductJpaMapper;
import application.adapters.persistence.jpa.mappers.SellerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataProductRepository;
import application.adapters.persistence.jpa.repositories.SpringDataProductVariantRepository;
import application.adapters.persistence.jpa.repositories.SpringDataSellerRepository;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.ProductStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ProductJpaAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository productRepository;
    private final SpringDataProductVariantRepository variantRepository;
    private final SpringDataSellerRepository sellerRepository;

    public ProductJpaAdapter(SpringDataProductRepository productRepository,
                             SpringDataProductVariantRepository variantRepository,
                             SpringDataSellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public Product save(Product product) {
        if (product.getIdentifier() == null || product.getIdentifier().isBlank()) {
            product.setIdentifier(IdentifierGenerator.next("PRD"));
        }
        productRepository.save(ProductJpaMapper.toEntity(product));
        return product;
    }

    @Override
    public void update(Product product) {
        productRepository.save(ProductJpaMapper.toEntity(product));
    }

    @Override
    public Optional<Product> findByIdentifier(Product product) {
        if (product == null || product.getIdentifier() == null) {
            return Optional.empty();
        }
        return productRepository.findById(product.getIdentifier()).map(this::toDomain);
    }

    @Override
    public Optional<Product> findByVariantSku(ProductVariant variant) {
        if (variant == null || variant.getSku() == null) {
            return Optional.empty();
        }
        return productRepository.findByVariantsSku(variant.getSku()).map(this::toDomain);
    }

    @Override
    public boolean existsBySku(ProductVariant variant) {
        return variant != null && variant.getSku() != null && variantRepository.existsById(variant.getSku());
    }

    @Override
    public List<Product> findBySeller(Seller seller) {
        return productRepository.findBySellerIdentification(seller.getIdentification()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Product> findPublished(ProductCategory category) {
        List<ProductJpaEntity> entities = category == null
                ? productRepository.findByStatus(ProductStatus.PUBLISHED.getCode())
                : productRepository.findByStatusAndCategory(ProductStatus.PUBLISHED.getCode(), category.getCode());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll().stream().map(this::toDomain).toList();
    }

    public Product toDomain(ProductJpaEntity entity) {
        Seller seller = sellerRepository.findById(entity.getSellerIdentification())
                .map(SellerJpaMapper::toDomain).orElse(null);
        return ProductJpaMapper.toDomain(entity, seller);
    }
}
