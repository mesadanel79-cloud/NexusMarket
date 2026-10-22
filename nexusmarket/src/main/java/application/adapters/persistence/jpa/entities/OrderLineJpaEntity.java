package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * JPA persistence entity (repository DTO): Lines of an order.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "order_lines")
public class OrderLineJpaEntity {

    @Id
    @Column(name = "order_line_id", length = 50, nullable = false)
    private String orderLineId;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @Column(name = "product_identifier", length = 30, nullable = false)
    private String productIdentifier;

    @Column(name = "product_name", length = 150, nullable = false)
    private String productName;

    @Column(name = "product_type", length = 15, nullable = false)
    private String productType;

    @Column(name = "sku", length = 40, nullable = false)
    private String sku;

    @Column(name = "variant_description", length = 150)
    private String variantDescription;

    @Column(name = "seller_identification", length = 20, nullable = false)
    private String sellerIdentification;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPriceAmount;

    @Column(name = "currency", length = 5, nullable = false)
    private String currency;

    @Column(name = "warehouse_identifier", length = 20)
    private String warehouseIdentifier;

    public String getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(String orderLineId) {
        this.orderLineId = orderLineId;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getProductIdentifier() {
        return productIdentifier;
    }

    public void setProductIdentifier(String productIdentifier) {
        this.productIdentifier = productIdentifier;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getVariantDescription() {
        return variantDescription;
    }

    public void setVariantDescription(String variantDescription) {
        this.variantDescription = variantDescription;
    }

    public String getSellerIdentification() {
        return sellerIdentification;
    }

    public void setSellerIdentification(String sellerIdentification) {
        this.sellerIdentification = sellerIdentification;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPriceAmount() {
        return unitPriceAmount;
    }

    public void setUnitPriceAmount(BigDecimal unitPriceAmount) {
        this.unitPriceAmount = unitPriceAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getWarehouseIdentifier() {
        return warehouseIdentifier;
    }

    public void setWarehouseIdentifier(String warehouseIdentifier) {
        this.warehouseIdentifier = warehouseIdentifier;
    }
}
