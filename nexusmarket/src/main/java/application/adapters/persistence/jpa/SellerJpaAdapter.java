package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.mappers.SellerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataSellerRepository;
import application.domain.models.Seller;
import application.domain.ports.out.SellerRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SellerJpaAdapter implements SellerRepositoryPort {

    private final SpringDataSellerRepository sellerRepository;

    public SellerJpaAdapter(SpringDataSellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Override
    public Seller save(Seller seller) {
        sellerRepository.save(SellerJpaMapper.toEntity(seller));
        return seller;
    }

    @Override
    public void update(Seller seller) {
        sellerRepository.save(SellerJpaMapper.toEntity(seller));
    }

    @Override
    public Optional<Seller> findByIdentification(Seller seller) {
        if (seller == null || seller.getIdentification() == null) {
            return Optional.empty();
        }
        return sellerRepository.findById(seller.getIdentification()).map(SellerJpaMapper::toDomain);
    }

    @Override
    public boolean existsByIdentification(Seller seller) {
        return seller.getIdentification() != null && sellerRepository.existsById(seller.getIdentification());
    }

    @Override
    public boolean existsByEmail(Seller seller) {
        return seller.getEmail() != null && sellerRepository.existsByEmailIgnoreCase(seller.getEmail());
    }

    @Override
    public List<Seller> findAll() {
        return sellerRepository.findAll().stream().map(SellerJpaMapper::toDomain).toList();
    }
}
