package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.amount;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.money;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.valueobjects.InvoiceStatus;

public final class InvoiceJpaMapper {

    private InvoiceJpaMapper() {
    }

    public static InvoiceJpaEntity toEntity(Invoice domain) {
        InvoiceJpaEntity entity = new InvoiceJpaEntity();
        entity.setIdentifier(domain.getIdentifier());
        entity.setOrderIdentifier(domain.getOrder().getIdentifier());
        entity.setBuyerIdentification(domain.getBuyer().getIdentification());
        entity.setIssueDate(domain.getIssueDate());
        entity.setSubtotal(amount(domain.getSubtotal()));
        entity.setTaxAmount(amount(domain.getTaxAmount()));
        entity.setTotal(amount(domain.getTotal()));
        entity.setRefundedAmount(amount(domain.getRefundedAmount()));
        entity.setCurrency(domain.getTotal().getCurrency().getCode());
        entity.setStatus(code(domain.getStatus()));
        return entity;
    }

    public static Invoice toDomain(InvoiceJpaEntity entity, Order order, Buyer buyer) {
        Invoice domain = new Invoice();
        domain.setIdentifier(entity.getIdentifier());
        domain.setOrder(order);
        domain.setBuyer(buyer);
        domain.setIssueDate(entity.getIssueDate());
        domain.setSubtotal(money(entity.getSubtotal(), entity.getCurrency()));
        domain.setTaxAmount(money(entity.getTaxAmount(), entity.getCurrency()));
        domain.setTotal(money(entity.getTotal(), entity.getCurrency()));
        domain.setRefundedAmount(money(entity.getRefundedAmount(), entity.getCurrency()));
        domain.setStatus(InvoiceStatus.fromCode(entity.getStatus()));
        return domain;
    }
}
