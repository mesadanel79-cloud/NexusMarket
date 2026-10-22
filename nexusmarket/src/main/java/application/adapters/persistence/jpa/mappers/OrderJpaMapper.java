package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.amount;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.currency;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.money;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.entities.OrderLineJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.ProductType;
import java.util.ArrayList;
import java.util.List;

/**
 * Order mapping. Each line keeps a snapshot of product name/type, SKU, seller, unit
 * price and allocated warehouse, so reading an order never depends on the current
 * state of the catalog.
 */
public final class OrderJpaMapper {

    private OrderJpaMapper() {
    }

    public static OrderJpaEntity toEntity(Order domain) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setIdentifier(domain.getIdentifier());
        entity.setBuyerIdentification(domain.getBuyer().getIdentification());
        entity.setStatus(code(domain.getStatus()));
        entity.setTotalAmount(amount(domain.getTotalAmount()));
        entity.setCurrency(currency(domain.getTotalAmount()));
        entity.setShippingAddress(domain.getShippingAddress());
        entity.setPaymentMethod(code(domain.getPaymentMethod()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setPaidAt(domain.getPaidAt());
        entity.setShippedAt(domain.getShippedAt());
        entity.setDeliveredAt(domain.getDeliveredAt());
        entity.setCancelledAt(domain.getCancelledAt());
        List<OrderLineJpaEntity> lines = new ArrayList<>();
        for (OrderLine line : domain.getLines()) {
            OrderLineJpaEntity lineEntity = new OrderLineJpaEntity();
            lineEntity.setOrderLineId(domain.getIdentifier() + "-" + line.getLineNumber());
            lineEntity.setLineNumber(line.getLineNumber());
            lineEntity.setProductIdentifier(line.getProduct().getIdentifier());
            lineEntity.setProductName(line.getProduct().getName());
            lineEntity.setProductType(code(line.getProduct().getProductType()));
            lineEntity.setSku(line.getSku());
            lineEntity.setVariantDescription(line.getVariant().getDescription());
            lineEntity.setSellerIdentification(line.getSeller() == null ? null : line.getSeller().getIdentification());
            lineEntity.setQuantity(line.getQuantity());
            lineEntity.setUnitPriceAmount(amount(line.getUnitPrice()));
            lineEntity.setCurrency(currency(line.getUnitPrice()));
            lineEntity.setWarehouseIdentifier(line.getWarehouse() == null ? null : line.getWarehouse().getIdentifier());
            lines.add(lineEntity);
        }
        entity.setLines(lines);
        return entity;
    }

    public static Order toDomain(OrderJpaEntity entity, Buyer buyer) {
        Order domain = new Order();
        domain.setIdentifier(entity.getIdentifier());
        domain.setBuyer(buyer);
        domain.setStatus(OrderStatus.fromCode(entity.getStatus()));
        domain.setTotalAmount(money(entity.getTotalAmount(), entity.getCurrency()));
        domain.setShippingAddress(entity.getShippingAddress());
        domain.setPaymentMethod(PaymentMethod.fromCode(entity.getPaymentMethod()));
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setPaidAt(entity.getPaidAt());
        domain.setShippedAt(entity.getShippedAt());
        domain.setDeliveredAt(entity.getDeliveredAt());
        domain.setCancelledAt(entity.getCancelledAt());
        List<OrderLine> lines = new ArrayList<>();
        for (OrderLineJpaEntity lineEntity : entity.getLines()) {
            Product product = new Product();
            product.setIdentifier(lineEntity.getProductIdentifier());
            product.setName(lineEntity.getProductName());
            product.setProductType(ProductType.fromCode(lineEntity.getProductType()));
            ProductVariant variant = new ProductVariant();
            variant.setSku(lineEntity.getSku());
            variant.setDescription(lineEntity.getVariantDescription());
            variant.setPrice(money(lineEntity.getUnitPriceAmount(), lineEntity.getCurrency()));
            Seller seller = new Seller();
            seller.setIdentification(lineEntity.getSellerIdentification());
            OrderLine line = new OrderLine();
            line.setLineNumber(lineEntity.getLineNumber());
            line.setProduct(product);
            line.setVariant(variant);
            line.setSeller(seller);
            line.setQuantity(lineEntity.getQuantity());
            line.setUnitPrice(money(lineEntity.getUnitPriceAmount(), lineEntity.getCurrency()));
            line.setWarehouse(WarehouseJpaMapper.reference(lineEntity.getWarehouseIdentifier()));
            lines.add(line);
        }
        domain.setLines(lines);
        return domain;
    }
}
