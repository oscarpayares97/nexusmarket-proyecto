package application.domain.services.seller;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.Guard;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.RegisterSellerUseCase;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;

public class RegisterSellerService implements RegisterSellerUseCase {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepositoryPort userRepository;
    private final SellerRepositoryPort sellerRepository;
    private final WarehouseRepositoryPort warehouseRepository;
    private final PasswordServicePort passwordService;
    private final ValidateRoleAuthorizationService authorization;

    public RegisterSellerService(UserRepositoryPort userRepository,
                                 SellerRepositoryPort sellerRepository,
                                 WarehouseRepositoryPort warehouseRepository,
                                 PasswordServicePort passwordService,
                                 ValidateRoleAuthorizationService authorization) {
        this.userRepository = userRepository;
        this.sellerRepository = sellerRepository;
        this.warehouseRepository = warehouseRepository;
        this.passwordService = passwordService;
        this.authorization = authorization;
    }

    @Override
    public Seller register(User actor, Command command) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);

        // Validar todo antes de guardar nada
        Guard.requireText(command.tradeName(), "El nombre comercial");
        Guard.requireText(command.warehouseName(), "Nombre del alamacen");
        Guard.requireText(command.warehouseLocation(), "Localizacion del alamacen");
        if (command.rawPassword() == null || command.rawPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidDataException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (userRepository.existsByDocument(command.document())) {
            throw new DuplicateEntityException("Ya existe un usuario con ese documento..");
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateEntityException("Ya existe un usuario con esa dirección de correo electrónico.");
        }

        User user = new User(null, command.document(), command.fullName(), command.email(),
                passwordService.encode(command.rawPassword()), UserRole.SELLER);
        User savedUser = userRepository.save(user);
        Seller seller = sellerRepository.save(new Seller(savedUser.getId(), command.tradeName()));

        // Incorporación: el vendedor nace con su primera bodega
        warehouseRepository.save(new Warehouse(null, command.warehouseName(),
                command.warehouseLocation(), WarehouseType.SELLER, savedUser.getId()));
        return seller;
    }
}