package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.mappers.BuyerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataBuyerRepository;
import application.domain.models.Buyer;
import application.domain.ports.out.BuyerRepositoryPort;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BuyerJpaAdapter implements BuyerRepositoryPort {

    private final SpringDataBuyerRepository buyerRepository;

    public BuyerJpaAdapter(SpringDataBuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    @Override
    public Buyer save(Buyer buyer) {
        buyerRepository.save(BuyerJpaMapper.toEntity(buyer));
        return buyer;
    }

    @Override
    public void update(Buyer buyer) {
        buyerRepository.save(BuyerJpaMapper.toEntity(buyer));
    }

    @Override
    public Optional<Buyer> findByIdentification(Buyer buyer) {
        if (buyer == null || buyer.getIdentification() == null) {
            return Optional.empty();
        }
        return buyerRepository.findById(buyer.getIdentification()).map(BuyerJpaMapper::toDomain);
    }

    @Override
    public boolean existsByIdentification(Buyer buyer) {
        return buyer.getIdentification() != null && buyerRepository.existsById(buyer.getIdentification());
    }

    @Override
    public boolean existsByEmail(Buyer buyer) {
        return buyer.getEmail() != null && buyerRepository.existsByEmailIgnoreCase(buyer.getEmail());
    }
}
