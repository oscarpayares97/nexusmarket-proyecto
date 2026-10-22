package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.adapters.persistence.jpa.mappers.UserJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaUserRepository;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Adaptador de salida: implementa el puerto del dominio usando Spring Data JPA (MySQL). */
@Repository
public class UserJpaAdapter implements UserRepositoryPort {

    private final SpringDataJpaUserRepository repository;

    public UserJpaAdapter(SpringDataJpaUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = repository.save(UserJpaMapper.toEntity(user));
        if (user.getId() == null) {
            user.assignId(saved.getId());
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return repository.findByEmail(email.trim().toLowerCase()).map(UserJpaMapper::toDomain);
    }

    @Override
    public boolean existsByDocument(String document) {
        return document != null && repository.existsByDocument(document.trim());
    }

    @Override
    public boolean existsByEmail(String email) {
        return email != null && repository.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(UserJpaMapper::toDomain).toList();
    }
}