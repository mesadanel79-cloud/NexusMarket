package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Integer> {

    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByIdentification(String identification);

    boolean existsByEmailIgnoreCase(String email);
}
