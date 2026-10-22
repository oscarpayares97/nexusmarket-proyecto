package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring genera la implementación a partir de los nombres de los métodos. */
public interface SpringDataJpaUserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByDocument(String document);

    boolean existsByEmail(String email);
}