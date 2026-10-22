package application.domain.ports.out;

import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.valueobjects.ProductCategory;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {

    Product save(Product product);

    void update(Product product);

    Optional<Product> findByIdentifier(Product product);

    Optional<Product> findByVariantSku(ProductVariant variant);

    boolean existsBySku(ProductVariant variant);

    List<Product> findBySeller(Seller seller);

    List<Product> findPublished(ProductCategory category);

    List<Product> findAll();
}
