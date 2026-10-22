package application.domain.ports.out;

import application.domain.models.Seller;
import java.util.List;
import java.util.Optional;

public interface SellerRepositoryPort {

    Seller save(Seller seller);

    void update(Seller seller);

    Optional<Seller> findByIdentification(Seller seller);

    boolean existsByIdentification(Seller seller);

    boolean existsByEmail(Seller seller);

    List<Seller> findAll();
}
