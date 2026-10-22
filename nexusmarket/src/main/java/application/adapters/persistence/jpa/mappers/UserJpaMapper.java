package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.User;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;

public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static UserJpaEntity toEntity(User domain) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setDocument(domain.getDocument().value());
        entity.setFullName(domain.getFullName());
        entity.setEmail(domain.getEmail().value());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setRole(domain.getRole().name());
        entity.setStatus(domain.getStatus().name());
        return entity;
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getDocument(), entity.getFullName(),
                entity.getEmail(), entity.getPasswordHash(),
                UserRole.valueOf(entity.getRole()), UserStatus.valueOf(entity.getStatus()));
    }
}