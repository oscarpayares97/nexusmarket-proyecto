package application.domain.services.buyer;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuyerManagementServicesTest {

    private AddBuyerAddressService addAddress;
    private ChangeBuyerCommercialStatusService changeStatus;
    private Buyer buyerLuis;

    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User marta = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @BeforeEach
    void setUp() {
        BuyerRepositoryPort buyers = mock(BuyerRepositoryPort.class);
        buyerLuis = new Buyer(5L, "Street 10 #5-20");
        when(buyers.findByUserId(5L)).thenReturn(Optional.of(buyerLuis));
        when(buyers.save(any(Buyer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ValidateRoleAuthorizationService authorization = new ValidateRoleAuthorizationService();
        addAddress = new AddBuyerAddressService(buyers, authorization);
        changeStatus = new ChangeBuyerCommercialStatusService(buyers, authorization);
    }

    @Test
    void addsAnAdditionalAddress() {
        addAddress.add(luis, "Office 30 #1-1");
        assertTrue(buyerLuis.hasAddress("Office 30 #1-1"));
    }

    @Test
    void onlyBuyersManageTheirAddresses() {
        assertThrows(UnauthorizedOperationException.class, () -> addAddress.add(marta, "Street 1"));
    }

    @Test
    void theAdministratorSuspendsABuyer() {
        changeStatus.change(admin, 5L, CommercialStatus.SUSPENDED);
        assertFalse(buyerLuis.canPurchase());
    }
}