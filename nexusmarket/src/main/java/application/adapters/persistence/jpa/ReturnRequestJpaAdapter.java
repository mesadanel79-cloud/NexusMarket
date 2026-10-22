package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ReturnRequestJpaEntity;
import application.adapters.persistence.jpa.mappers.ReturnRequestJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataOrderRepository;
import application.adapters.persistence.jpa.repositories.SpringDataReturnRequestRepository;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.ReturnRequestRepositoryPort;
import application.domain.valueobjects.ReturnStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ReturnRequestJpaAdapter implements ReturnRequestRepositoryPort {

    private final SpringDataReturnRequestRepository returnRepository;
    private final SpringDataOrderRepository orderRepository;
    private final OrderJpaAdapter orderJpaAdapter;

    public ReturnRequestJpaAdapter(SpringDataReturnRequestRepository returnRepository,
                                   SpringDataOrderRepository orderRepository, OrderJpaAdapter orderJpaAdapter) {
        this.returnRepository = returnRepository;
        this.orderRepository = orderRepository;
        this.orderJpaAdapter = orderJpaAdapter;
    }

    @Override
    public ReturnRequest save(ReturnRequest returnRequest) {
        if (returnRequest.getIdentifier() == null || returnRequest.getIdentifier().isBlank()) {
            returnRequest.setIdentifier(IdentifierGenerator.next("RET"));
        }
        returnRepository.save(ReturnRequestJpaMapper.toEntity(returnRequest));
        return returnRequest;
    }

    @Override
    public void update(ReturnRequest returnRequest) {
        returnRepository.save(ReturnRequestJpaMapper.toEntity(returnRequest));
    }

    @Override
    public Optional<ReturnRequest> findByIdentifier(ReturnRequest returnRequest) {
        if (returnRequest == null || returnRequest.getIdentifier() == null) {
            return Optional.empty();
        }
        return returnRepository.findById(returnRequest.getIdentifier()).map(this::toDomain);
    }

    @Override
    public List<ReturnRequest> findByOrder(Order order) {
        return returnRepository.findByOrderIdentifier(order.getIdentifier()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ReturnRequest> findByBuyer(Buyer buyer) {
        return returnRepository.findByBuyerIdentificationOrderByRequestedAtDesc(buyer.getIdentification()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<ReturnRequest> findByStatus(ReturnStatus status) {
        return returnRepository.findByStatusOrderByRequestedAtAsc(status.getCode()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<ReturnRequest> findAll() {
        return returnRepository.findAll().stream().map(this::toDomain).toList();
    }

    private ReturnRequest toDomain(ReturnRequestJpaEntity entity) {
        Order order = orderRepository.findById(entity.getOrderIdentifier()).map(orderJpaAdapter::toDomain)
                .orElseThrow(() -> new IllegalStateException("Order " + entity.getOrderIdentifier() + " not found."));
        return ReturnRequestJpaMapper.toDomain(entity, order, order.getBuyer());
    }
}
