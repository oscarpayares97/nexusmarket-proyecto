package application.adapters.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import application.adapters.rest.dtos.responses.ResponseDTOs.OrderResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ProductResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.UserResponse;
import application.adapters.rest.mappers.DomainRestMapper;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import java.util.List;
import org.junit.jupiter.api.Test;

class DomainRestMapperTest {

    @Test
    void userResponseNeverExposesThePasswordHash() {
        User user = new User(7L, "1017123456", "Ana Perez", "ana@nexusmarket.com", "SECRET-HASH",
                UserRole.BUYER);

        UserResponse response = DomainRestMapper.toUser(user);

        assertEquals(7L, response.id());
        assertEquals("ana@nexusmarket.com", response.email());
        assertEquals("BUYER", response.role());
        assertEquals(6, UserResponse.class.getRecordComponents().length);
    }

    @Test
    void productResponseCarriesPriceAsPlainNumber() {
        Product product = new Product(1L, 2L, "Mouse", ProductType.PHYSICAL, Money.of("50000"));

        ProductResponse response = DomainRestMapper.toProduct(product);

        assertEquals("Mouse", response.name());
        assertEquals("PHYSICAL", response.type());
        assertEquals(0, product.getPrice().amount().compareTo(response.price()));
    }

    @Test
    void emptyCartMapsToOrderWithoutItems() {
        Order order = new Order(3L, 5L);

        OrderResponse response = DomainRestMapper.toOrder(order);

        assertEquals(5L, response.buyerId());
        assertEquals(order.getStatus().name(), response.status());
        assertEquals(0, response.items().size());
        assertNull(response.deliveryAddress());
    }

    @Test
    void mapListKeepsOrderAndSize() {
        List<String> result = DomainRestMapper.mapList(List.of(1, 2, 3), n -> "n" + n);

        assertEquals(List.of("n1", "n2", "n3"), result);
    }
}