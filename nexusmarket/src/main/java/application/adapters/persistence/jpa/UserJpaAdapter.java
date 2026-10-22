package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.adapters.persistence.jpa.mappers.BuyerJpaMapper;
import application.adapters.persistence.jpa.mappers.SellerJpaMapper;
import application.adapters.persistence.jpa.mappers.UserJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataBuyerRepository;
import application.adapters.persistence.jpa.repositories.SpringDataSellerRepository;
import application.adapters.persistence.jpa.repositories.SpringDataUserRepository;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserJpaAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository userRepository;
    private final SpringDataBuyerRepository buyerRepository;
    private final SpringDataSellerRepository sellerRepository;

    public UserJpaAdapter(SpringDataUserRepository userRepository, SpringDataBuyerRepository buyerRepository,
                          SpringDataSellerRepository sellerRepository) {
        this.userRepository = userRepository;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = userRepository.save(UserJpaMapper.toEntity(user));
        user.setUserId(saved.getUserId());
        return user;
    }

    @Override
    public void update(User user) {
        userRepository.save(UserJpaMapper.toEntity(user));
    }

    @Override
    public Optional<User> findById(User user) {
        if (user == null || user.getUserId() == null) {
            return Optional.empty();
        }
        return userRepository.findById(user.getUserId()).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(User user) {
        if (user == null || user.getUsername() == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(user.getUsername()).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(User user) {
        return user.getUsername() != null && userRepository.existsByUsername(user.getUsername());
    }

    @Override
    public boolean existsByIdentification(User user) {
        return user.getIdentification() != null && userRepository.existsByIdentification(user.getIdentification());
    }

    @Override
    public boolean existsByEmail(User user) {
        return user.getEmail() != null && userRepository.existsByEmailIgnoreCase(user.getEmail());
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream().map(this::toDomain).toList();
    }

    private User toDomain(UserJpaEntity entity) {
        Buyer buyer = entity.getBuyerIdentification() == null ? null
                : buyerRepository.findById(entity.getBuyerIdentification()).map(BuyerJpaMapper::toDomain).orElse(null);
        Seller seller = entity.getSellerIdentification() == null ? null
                : sellerRepository.findById(entity.getSellerIdentification()).map(SellerJpaMapper::toDomain).orElse(null);
        return UserJpaMapper.toDomain(entity, buyer, seller);
    }
}
