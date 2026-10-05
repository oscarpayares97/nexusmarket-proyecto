package application.domain.services.seller;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.RegisterSellerUseCase.Command;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterSellerServiceTest {

    private UserRepositoryPort users;
    private SellerRepositoryPort sellers;
    private WarehouseRepositoryPort warehouses;
    private RegisterSellerService service;

    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User seller = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);

    @BeforeEach
    void setUp() {
        users = mock(UserRepositoryPort.class);
        sellers = mock(SellerRepositoryPort.class);
        warehouses = mock(WarehouseRepositoryPort.class);
        PasswordServicePort passwords = mock(PasswordServicePort.class);

        when(passwords.encode(anyString())).thenReturn("hash");
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.assignId(10L);
            return saved;
        });
        when(sellers.save(any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(warehouses.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service = new RegisterSellerService(users, sellers, warehouses, passwords,
                new ValidateRoleAuthorizationService());
    }

    private Command command() {
        return new Command("3001", "Pedro Mora", "pedro@mail.com", "secret123",
                "Pedro Store", "Main warehouse", "Industrial zone");
    }

    @Test
    void registersTheSellerWithItsFirstWarehouse() {
        Seller registered = service.register(admin, command());
        assertEquals(10L, registered.getUserId());
        verify(warehouses).save(any(Warehouse.class));
    }

    @Test
    void onlyAdministratorsRegisterSellers() {
        assertThrows(UnauthorizedOperationException.class, () -> service.register(seller, command()));
        verify(users, never()).save(any(User.class));
    }

    @Test
    void rejectsADuplicatedDocument() {
        when(users.existsByDocument("3001")).thenReturn(true);
        assertThrows(DuplicateEntityException.class, () -> service.register(admin, command()));
    }
}