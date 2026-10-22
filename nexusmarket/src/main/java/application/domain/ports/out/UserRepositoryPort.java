package application.domain.ports.out;

import application.domain.models.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    void update(User user);

    Optional<User> findById(User user);

    Optional<User> findByUsername(User user);

    boolean existsByUsername(User user);

    boolean existsByIdentification(User user);

    boolean existsByEmail(User user);

    List<User> findAll();
}
