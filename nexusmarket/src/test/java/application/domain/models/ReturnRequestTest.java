package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReturnRequestTest {

    private OrderItem shirtLine() {
        return new OrderItem(1L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"), 3, 7L);
    }

    private ReturnRequest returnOf(int units) {
        return new ReturnRequest(1L, 10L, 5L, List.of(ReturnItem.from(shirtLine(), units)), "Wrong size");
    }

    @Test
    void subtotalMultipliesPriceByUnits() {
        assertEquals(Money.of("70000"), ReturnItem.from(shirtLine(), 2).subtotal());
    }

    @Test
    void cannotReturnMoreThanPurchased() {
        assertThrows(InvalidDataException.class, () -> ReturnItem.from(shirtLine(), 4));
    }

    @Test
    void digitalProductsCannotBeReturned() {
        OrderItem ebook = new OrderItem(2L, "Ebook", ProductType.DIGITAL, Money.of("50000"), 1, null);
        assertThrows(InvalidDataException.class, () -> ReturnItem.from(ebook, 1));
    }

    @Test
    void lifecycleGoesFromRequestedToReceived() {
        ReturnRequest request = returnOf(2);
        assertEquals(ReturnStatus.REQUESTED, request.getStatus());
        request.approve();
        request.markReceived();
        assertEquals(ReturnStatus.RECEIVED, request.getStatus());
        assertEquals(Money.of("70000"), request.total());
    }

    @Test
    void cannotRejectAnApprovedReturn() {
        ReturnRequest request = returnOf(1);
        request.approve();
        assertThrows(InvalidStatusTransitionException.class, () -> request.reject("Too late"));
    }

    @Test
    void refundExistsOnlyForReceivedReturnsAndIsProcessedOnce() {
        ReturnRequest request = returnOf(2);
        assertThrows(InvalidStatusTransitionException.class, () -> Refund.forReturn(request));
        request.approve();
        request.markReceived();
        Refund refund = Refund.forReturn(request);
        assertEquals(Money.of("70000"), refund.getAmount());
        refund.process();
        assertEquals(RefundStatus.PROCESSED, refund.getStatus());
        assertThrows(InvalidStatusTransitionException.class, () -> refund.process());
    }
}