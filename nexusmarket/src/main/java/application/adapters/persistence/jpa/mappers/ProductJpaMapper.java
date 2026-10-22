package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.amount;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.currency;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.money;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.entities.ProductVariantJpaEntity;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import java.util.ArrayList;
import java.util.List;

public final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    public static ProductJpaEntity toEntity(Product domain) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setIdentifier(domain.getIdentifier());
        entity.setSellerIdentification(domain.getSeller() == null ? null : domain.getSeller().getIdentification());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setCategory(code(domain.getCategory()));
        entity.setProductType(code(domain.getProductType()));
        entity.setStatus(code(domain.getStatus()));
        entity.setCreatedAt(domain.getCreatedAt());
        List<ProductVariantJpaEntity> variants = new ArrayList<>();
        for (ProductVariant variant : domain.getVariants()) {
            ProductVariantJpaEntity variantEntity = new ProductVariantJpaEntity();
            variantEntity.setSku(variant.getSku());
            variantEntity.setDescription(variant.getDescription());
            variantEntity.setPriceAmount(amount(variant.getPrice()));
            variantEntity.setCurrency(currency(variant.getPrice()));
            variants.add(variantEntity);
        }
        entity.setVariants(variants);
        return entity;
    }

    public static Product toDomain(ProductJpaEntity entity, Seller seller) {
        if (entity == null) {
            return null;
        }
        Product domain = new Product();
        domain.setIdentifier(entity.getIdentifier());
        domain.setSeller(seller);
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setCategory(ProductCategory.fromCode(entity.getCategory()));
        domain.setProductType(ProductType.fromCode(entity.getProductType()));
        domain.setStatus(ProductStatus.fromCode(entity.getStatus()));
        domain.setCreatedAt(entity.getCreatedAt());
        List<ProductVariant> variants = new ArrayList<>();
        for (ProductVariantJpaEntity variantEntity : entity.getVariants()) {
            ProductVariant variant = new ProductVariant();
            variant.setSku(variantEntity.getSku());
            variant.setDescription(variantEntity.getDescription());
            variant.setPrice(money(variantEntity.getPriceAmount(), variantEntity.getCurrency()));
            variants.add(variant);
        }
        domain.setVariants(variants);
        return domain;
    }
}
