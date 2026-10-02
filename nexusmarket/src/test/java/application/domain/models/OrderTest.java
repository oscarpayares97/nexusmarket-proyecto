package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    private Product shirt() {
        Product product = new Product(null, 1L, "Basic T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        product.assignId(1L);
        return product;
    }

    private Product ebook() {
        Product product = new Product(null, 1L, "Java course (ebook)", ProductType.DIGITAL, Money.of("50000"));
        product.assignId(2L);
        return product;
    }

    @Test
    void totalAddsAllLines() {
        Order order = new Order(null, 10L);
        order.addItem(shirt(), 3);
        order.addItem(ebook(), 1);
        assertEquals(Money.of("155000"), order.total());
    }

    @Test
    void addingTheSameProductTwiceAddsUnits() {
        Order order = new Order(null, 10L);
        order.addItem(shirt(), 2);
        order.addItem(shirt(), 1);
        assertEquals(1, order.getItems().size());
        assertEquals(3, order.getItems().get(0).getQuantity());
    }

    @Test
    void cannotConfirmAnEmptyCart() {
        Order order = new Order(null, 10L);
        assertThrows(InvalidDataException.class, () -> order.confirm("Street 1"));
    }

    @Test
    void lifecycleReachesCompleted() {
        Order order = new Order(null, 10L);
        order.addItem(shirt(), 1);
        order.confirm("Calle 1");
        order.markPaid();
        order.markDispatched();
        order.markCompleted();
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void completedOrderCannotBeModified() {
        Order order = new Order(null, 10L);
        order.addItem(shirt(), 1);
        order.confirm("Calle 1");
        order.markPaid();
        order.markDispatched();
        order.markCompleted();
        assertThrows(InvalidStatusTransitionException.class, () -> order.addItem(shirt(), 1));
    }

    @Test
    void cannotDispatchAnUnpaidOrder() {
        Order order = new Order(null, 10L);
        order.addItem(shirt(), 1);
        order.confirm("Calle 1");
        assertThrows(InvalidStatusTransitionException.class, () -> order.markDispatched());
    }
}