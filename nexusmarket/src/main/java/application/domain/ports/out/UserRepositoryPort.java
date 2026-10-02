package application.domain.ports.out;

import application.domain.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByDocument(String document);

    boolean existsByEmail(String email);

    List<User> findAll();
}