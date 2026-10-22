package application.adapters.persistence.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity (repository DTO): Catalog products.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "products")
public class ProductJpaEntity {

    @Id
    @Column(name = "identifier", length = 30, nullable = false)
    private String identifier;

    @Column(name = "seller_identification", length = 20, nullable = false)
    private String sellerIdentification;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "category", length = 30, nullable = false)
    private String category;

    @Column(name = "product_type", length = 15, nullable = false)
    private String productType;

    @Column(name = "status", length = 15, nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_identifier")
    @OrderBy
    private List<ProductVariantJpaEntity> variants = new ArrayList<>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSellerIdentification() {
        return sellerIdentification;
    }

    public void setSellerIdentification(String sellerIdentification) {
        this.sellerIdentification = sellerIdentification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProductVariantJpaEntity> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantJpaEntity> variants) {
        this.variants = variants;
    }
}
