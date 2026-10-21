package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ChangeUserPasswordService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepositoryPort userRepository;
    private final PasswordServicePort passwordService;
    private final ValidateRoleAuthorizationService authorization;

    public ChangeUserPasswordService(UserRepositoryPort userRepository,
                                     PasswordServicePort passwordService,
                                     ValidateRoleAuthorizationService authorization) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.authorization = authorization;
    }

    public void change(User actor, String currentPassword, String newPassword) {
        authorization.requireRole(actor, UserRole.values());
        User user = userRepository.findById(actor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
        if (currentPassword == null || !passwordService.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("L contraseña no es correcta.");
        }
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidDataException("La nueva contraseña tiene mas de 8 caracteres.");
        }
        user.changePasswordHash(passwordService.encode(newPassword));
        userRepository.save(user);
    }
}