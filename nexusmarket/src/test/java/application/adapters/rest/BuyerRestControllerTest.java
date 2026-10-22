package application.adapters.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.adapters.rest.controllers.BuyerRestController;
import application.adapters.rest.dtos.requests.RequestDTOs.AddCartItemRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.ChangePasswordRequest;
import application.adapters.rest.dtos.responses.ResponseDTOs.OrderResponse;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.Test;

class BuyerRestControllerTest {

    private final BuyerPort port = mock(BuyerPort.class);
    private final BuyerRestController controller = new BuyerRestController(port);
    private final User actor =
            new User(4L, "1017123456", "Comprador", "comprador@nexusmarket.com", "hash", UserRole.BUYER);

    @Test
    void addItemToCartDelegatesToThePortAndMapsTheOrder() {
        when(port.addItemToCart(actor, 5L, 2)).thenReturn(new Order(9L, 4L));

        OrderResponse response = controller.addItemToCart(actor, new AddCartItemRequest(5L, 2));

        assertEquals(9L, response.id());
        assertEquals(4L, response.buyerId());
    }

    @Test
    void changePasswordDelegatesToThePort() {
        controller.changePassword(actor, new ChangePasswordRequest("old", "new"));

        verify(port).changePassword(actor, "old", "new");
    }
}