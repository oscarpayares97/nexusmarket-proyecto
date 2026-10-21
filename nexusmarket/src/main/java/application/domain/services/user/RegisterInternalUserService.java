package application.domain.services.user;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.Guard;
import application.domain.models.User;
import application.domain.ports.in.AdministratorPort.RegisterInternalUserCommand;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class RegisterInternalUserService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepositoryPort userRepository;
    private final PasswordServicePort passwordService;
    private final ValidateRoleAuthorizationService authorization;

    public RegisterInternalUserService(UserRepositoryPort userRepository,
                                       PasswordServicePort passwordService,
                                       ValidateRoleAuthorizationService authorization) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.authorization = authorization;
    }

    public User register(User actor, RegisterInternalUserCommand command) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);
        Guard.requireNonNull(command.role(), "The role");
        if (command.role() == UserRole.BUYER || command.role() == UserRole.SELLER) {
            throw new InvalidDataException("Los compradores y los vendedores tienen su propio registro.");
        }
        if (command.rawPassword() == null || command.rawPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidDataException("La contraseña debe tener al menos 8 caracteres..");
        }
        if (userRepository.existsByDocument(command.document())) {
            throw new DuplicateEntityException("Ya existe un usuario con ese documento.");
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateEntityException("Ya existe un usuario con esa dirección de correo electrónico..");
        }
        User user = new User(null, command.document(), command.fullName(), command.email(),
                passwordService.encode(command.rawPassword()), command.role());
        return userRepository.save(user);
    }
}