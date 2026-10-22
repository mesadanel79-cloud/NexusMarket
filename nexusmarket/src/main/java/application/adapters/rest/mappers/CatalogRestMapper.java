package application.adapters.rest.mappers;

import static application.adapters.rest.mappers.UserRestMapper.code;

import application.adapters.rest.dtos.requests.CreateProductRequestDTO;
import application.adapters.rest.dtos.requests.VariantRequestDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.VariantResponseDTO;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.ProductType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class CatalogRestMapper {

    private CatalogRestMapper() {
    }

    public static Product toDomain(CreateProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategory(ProductCategory.fromCode(dto.category()));
        product.setProductType(ProductType.fromCode(dto.productType()));
        List<ProductVariant> variants = new ArrayList<>();
        for (VariantRequestDTO variantDto : dto.variants()) {
            variants.add(toDomain(variantDto));
        }
        product.setVariants(variants);
        return product;
    }

    public static ProductVariant toDomain(VariantRequestDTO dto) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(dto.sku());
        variant.setDescription(dto.description());
        variant.setPrice(money(dto.price(), dto.currency()));
        return variant;
    }

    public static ProductVariant variantReference(String sku) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(sku);
        return variant;
    }

    public static Product reference(String productId) {
        Product product = new Product();
        product.setIdentifier(productId);
        return product;
    }

    public static Money money(BigDecimal amount, String currency) {
        return Money.of(amount, Currency.fromCode(currency));
    }

    public static ProductResponseDTO toResponse(Product product) {
        List<VariantResponseDTO> variants = product.getVariants().stream()
                .map(v -> new VariantResponseDTO(v.getSku(), v.getDescription(), v.getPrice().getAmount(),
                        v.getPrice().getCurrency().getCode()))
                .toList();
        return new ProductResponseDTO(product.getIdentifier(),
                product.getSeller() == null ? null : product.getSeller().getIdentification(),
                product.getSeller() == null ? null : product.getSeller().getStoreName(),
                product.getName(), product.getDescription(), code(product.getCategory()),
                code(product.getProductType()), code(product.getStatus()), product.getCreatedAt(), variants);
    }
}
