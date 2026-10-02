package application.domain.services.buyer;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.in.RegisterBuyerUseCase.Command;
import application.domain.valueobjects.SystemRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterBuyerServiceTest {

    private InMemoryUserRepository users;
    private InMemoryBuyerRepository buyers;
    private RegisterBuyerService service;

    @BeforeEach
    void setUp() {
        users = new InMemoryUserRepository();
        buyers = new InMemoryBuyerRepository();
        service = new RegisterBuyerService(users, buyers, new FakePasswordService());
    }

    private Command validCommand() {
        return new Command("1001", "Ana Gomez", "ana@mail.com", "secret123", "Street 10 #5-20");
    }

    @Test
    void registersABuyerWithTheBuyerRole() {
        Buyer buyer = service.register(validCommand());

        assertNotNull(buyer.getUserId());
        assertEquals("Street 10 #5-20", buyer.getPrimaryAddress());

        User user = users.findById(buyer.getUserId()).orElseThrow();
        assertEquals(SystemRole.BUYER, user.getRole());
        assertEquals("hashed:secret123", user.getPasswordHash());   // nunca se guarda la clave en claro
    }

    @Test
    void rejectsADuplicatedDocument() {
        service.register(validCommand());
        Command sameDocument = new Command("1001", "Otro", "other@mail.com", "secret123", "Street 1");
        assertThrows(DuplicateEntityException.class, () -> service.register(sameDocument));
    }

    @Test
    void rejectsADuplicatedEmail() {
        service.register(validCommand());
        Command sameEmail = new Command("2002", "Otro", "ana@mail.com", "secret123", "Street 1");
        assertThrows(DuplicateEntityException.class, () -> service.register(sameEmail));
    }

    @Test
    void rejectsAShortPassword() {
        Command shortPassword = new Command("1001", "Ana Gomez", "ana@mail.com", "123", "Street 1");
        assertThrows(InvalidDataException.class, () -> service.register(shortPassword));
    }

    @Test
    void doesNotCreateAUserWhenTheAddressIsBlank() {
        Command noAddress = new Command("1001", "Ana Gomez", "ana@mail.com", "secret123", " ");
        assertThrows(InvalidDataException.class, () -> service.register(noAddress));
        assertTrue(users.findAll().isEmpty());     // no quedó nada a medias
    }
}