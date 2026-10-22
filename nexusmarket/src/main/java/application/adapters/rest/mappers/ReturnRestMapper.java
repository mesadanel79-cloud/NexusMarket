package application.adapters.rest.mappers;

import static application.adapters.rest.mappers.UserRestMapper.code;

import application.adapters.rest.dtos.requests.CreateReturnRequestDTO;
import application.adapters.rest.dtos.responses.ReturnRequestResponseDTO;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.valueobjects.ReturnReason;

public final class ReturnRestMapper {

    private ReturnRestMapper() {
    }

    public static ReturnRequest toDomain(CreateReturnRequestDTO dto) {
        ReturnRequest request = new ReturnRequest();
        request.setOrder(OrderRestMapper.orderReference(dto.orderId()));
        request.setSku(dto.sku());
        request.setQuantity(dto.quantity());
        request.setReason(ReturnReason.fromCode(dto.reason()));
        request.setComment(dto.comment());
        return request;
    }

    public static ReturnRequest reference(String returnId) {
        ReturnRequest request = new ReturnRequest();
        request.setIdentifier(returnId);
        return request;
    }

    public static ReturnRequestResponseDTO toResponse(ReturnRequest request) {
        Refund refund = request.getRefund();
        return new ReturnRequestResponseDTO(request.getIdentifier(),
                request.getOrder() == null ? null : request.getOrder().getIdentifier(),
                request.getBuyer() == null ? null : request.getBuyer().getIdentification(), request.getSku(),
                request.getQuantity(), code(request.getReason()), request.getComment(), code(request.getStatus()),
                request.getRequestedAt(), request.getReviewedBy() == null ? null : request.getReviewedBy().getUserId(),
                request.getReviewedAt(), request.getRejectionReason(), request.getReceivedAt(),
                refund == null ? null : refund.getIdentifier(),
                refund == null || refund.getAmount() == null ? null : refund.getAmount().getAmount(),
                refund == null ? null : refund.getIssuedAt());
    }
}
