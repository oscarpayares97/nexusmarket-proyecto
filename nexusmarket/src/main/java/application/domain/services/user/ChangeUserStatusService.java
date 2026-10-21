package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.Guard;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;

public class ChangeUserStatusService {

    private final UserRepositoryPort userRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ChangeUserStatusService(UserRepositoryPort userRepository,
                                   ValidateRoleAuthorizationService authorization) {
        this.userRepository = userRepository;
        this.authorization = authorization;
    }

    public User change(User actor, Long userId, UserStatus newStatus) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);
        Guard.requireNonNull(newStatus, "El estado");
        if (actor.getId().equals(userId)) {
            throw new InvalidDataException("Un administrador no puede modificar su propio estado.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
        if (newStatus == UserStatus.ACTIVE) {
            user.activate();
        } else if (newStatus == UserStatus.BLOCKED) {
            user.block();
        } else {
            user.deactivate();
        }
        return userRepository.save(user);
    }
}