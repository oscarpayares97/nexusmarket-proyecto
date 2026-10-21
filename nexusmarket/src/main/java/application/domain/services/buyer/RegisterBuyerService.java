package application.domain.services.buyer;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.Buyer;
import application.domain.models.Guard;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserRole;
import application.domain.ports.in.PublicAccessPort.RegisterBuyerCommand;

public class RegisterBuyerService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepositoryPort userRepository;
    private final BuyerRepositoryPort buyerRepository;
    private final PasswordServicePort passwordService;

    public RegisterBuyerService(UserRepositoryPort userRepository,
                                BuyerRepositoryPort buyerRepository,
                                PasswordServicePort passwordService) {
        this.userRepository = userRepository;
        this.buyerRepository = buyerRepository;
        this.passwordService = passwordService;
    }

      public Buyer register(RegisterBuyerCommand command) {
        //  Validar todo antes de guardar
        Guard.requireText(command.primaryAddress(), "La primera direccion");
        if (command.rawPassword() == null || command.rawPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidDataException("La contraseña no puede tener mas de 8 caracteres.");
        }
        if (userRepository.existsByDocument(command.document())) {
            throw new DuplicateEntityException("Ya existe un usuario con ese documento.");
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateEntityException("Ya existe un usuario con este email.");
        }

        //  Crear el usuario (el modelo valida nombre, correo, etc.) y guardarlo
        String passwordHash = passwordService.encode(command.rawPassword());
        User user = new User(null, command.document(), command.fullName(),
                command.email(), passwordHash, UserRole.BUYER);
        User savedUser = userRepository.save(user);

        //  Crear y guardar el comprador asociado
        Buyer buyer = new Buyer(savedUser.getId(), command.primaryAddress());
        return buyerRepository.save(buyer);
    }
}