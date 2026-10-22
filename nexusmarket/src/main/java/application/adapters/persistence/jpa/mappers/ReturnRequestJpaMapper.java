package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.amount;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.currency;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.money;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.userId;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.userReference;

import application.adapters.persistence.jpa.entities.ReturnRequestJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.valueobjects.ReturnReason;
import application.domain.valueobjects.ReturnStatus;

public final class ReturnRequestJpaMapper {

    private ReturnRequestJpaMapper() {
    }

    public static ReturnRequestJpaEntity toEntity(ReturnRequest domain) {
        ReturnRequestJpaEntity entity = new ReturnRequestJpaEntity();
        entity.setIdentifier(domain.getIdentifier());
        entity.setOrderIdentifier(domain.getOrder().getIdentifier());
        entity.setBuyerIdentification(domain.getBuyer().getIdentification());
        entity.setSku(domain.getSku());
        entity.setQuantity(domain.getQuantity());
        entity.setReason(code(domain.getReason()));
        entity.setComment(domain.getComment());
        entity.setStatus(code(domain.getStatus()));
        entity.setRequestedAt(domain.getRequestedAt());
        entity.setReviewedByUserId(userId(domain.getReviewedBy()));
        entity.setReviewedAt(domain.getReviewedAt());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setReceivedByUserId(userId(domain.getReceivedBy()));
        entity.setReceivedAt(domain.getReceivedAt());
        Refund refund = domain.getRefund();
        if (refund != null) {
            entity.setRefundIdentifier(refund.getIdentifier());
            entity.setRefundAmount(amount(refund.getAmount()));
            entity.setRefundCurrency(currency(refund.getAmount()));
            entity.setRefundIssuedAt(refund.getIssuedAt());
            entity.setRefundIssuedByUserId(userId(refund.getIssuedBy()));
        }
        return entity;
    }

    public static ReturnRequest toDomain(ReturnRequestJpaEntity entity, Order order, Buyer buyer) {
        ReturnRequest domain = new ReturnRequest();
        domain.setIdentifier(entity.getIdentifier());
        domain.setOrder(order);
        domain.setBuyer(buyer);
        domain.setSku(entity.getSku());
        domain.setQuantity(entity.getQuantity());
        domain.setReason(ReturnReason.fromCode(entity.getReason()));
        domain.setComment(entity.getComment());
        domain.setStatus(ReturnStatus.fromCode(entity.getStatus()));
        domain.setRequestedAt(entity.getRequestedAt());
        domain.setReviewedBy(userReference(entity.getReviewedByUserId()));
        domain.setReviewedAt(entity.getReviewedAt());
        domain.setRejectionReason(entity.getRejectionReason());
        domain.setReceivedBy(userReference(entity.getReceivedByUserId()));
        domain.setReceivedAt(entity.getReceivedAt());
        if (entity.getRefundIdentifier() != null) {
            Refund refund = new Refund();
            refund.setIdentifier(entity.getRefundIdentifier());
            refund.setAmount(money(entity.getRefundAmount(), entity.getRefundCurrency()));
            refund.setIssuedAt(entity.getRefundIssuedAt());
            refund.setIssuedBy(userReference(entity.getRefundIssuedByUserId()));
            domain.setRefund(refund);
        }
        return domain;
    }
}
