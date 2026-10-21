package application.adapters.useCases;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.PublicAccessPort.RegisterBuyerCommand;
import application.domain.services.buyer.RegisterBuyerService;
import application.domain.services.catalog.ConsultPublishedProductsService;
import application.domain.services.catalog.PublishProductService;
import application.domain.services.catalog.RegisterProductService;
import application.domain.services.inventory.RegisterInventoryEntryService;
import application.domain.services.order.AddItemToCartService;
import application.domain.services.order.ConfirmOrderService;
import application.domain.services.order.PayOrderService;
import application.domain.services.returns.ApproveReturnService;
import application.domain.services.returns.ProcessRefundService;
import application.domain.services.returns.ReceiveReturnService;
import application.domain.services.returns.RejectReturnService;
import application.domain.services.returns.RequestReturnService;
import application.domain.services.seller.RegisterSellerService;
import application.domain.services.shipping.ConfirmDeliveryService;
import application.domain.services.shipping.DispatchShipmentService;
import application.domain.services.shipping.PackShipmentService;
import application.domain.services.shipping.RegisterShipmentInTransitService;
import application.domain.services.shipping.StartShipmentPreparationService;
import application.domain.services.warehouse.CreateWarehouseService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UseCaseImplDelegationTest {

    private final User admin = new User(1L, "1001", "Ana Admin", "admin@mail.com", "hash", UserRole.ADMINISTRATOR);
    private final User marta = new User(2L, "2001", "Marta Ruiz", "marta@mail.com", "hash", UserRole.SELLER);
    private final User oscar = new User(3L, "3001", "Oscar Vega", "oscar@mail.com", "hash", UserRole.LOGISTICS_OPERATOR);
    private final User luis = new User(5L, "1002", "Luis Perez", "luis@mail.com", "hash", UserRole.BUYER);

    @Test
    void publicAccessDelegatesTheBuyerRegistration() {
        RegisterBuyerService register = mock(RegisterBuyerService.class);
        PublicAccessUseCaseImpl impl =
                new PublicAccessUseCaseImpl(register, mock(ConsultPublishedProductsService.class));
        RegisterBuyerCommand command =
                new RegisterBuyerCommand("1001", "Ana Gomez", "ana@mail.com", "secret123", "Street 1");
        Buyer buyer = new Buyer(1L, "Street 1");
        when(register.register(command)).thenReturn(buyer);

        assertSame(buyer, impl.registerBuyer(command));
    }

    @Test
    void buyerDelegatesThePayment() {
        PayOrderService pay = mock(PayOrderService.class);
        BuyerUseCaseImpl impl = new BuyerUseCaseImpl(mock(AddItemToCartService.class),
                mock(ConfirmOrderService.class), pay, mock(RequestReturnService.class));
        Order order = new Order(10L, 5L);
        when(pay.pay(luis, 10L, true)).thenReturn(order);

        assertSame(order, impl.payOrder(luis, 10L, true));
    }

    @Test
    void sellerDelegatesThePublication() {
        PublishProductService publish = mock(PublishProductService.class);
        SellerUseCaseImpl impl = new SellerUseCaseImpl(mock(RegisterProductService.class), publish,
                mock(RegisterInventoryEntryService.class));
        Product product = new Product(1L, 2L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"));
        when(publish.publish(marta, 1L)).thenReturn(product);

        assertSame(product, impl.publishProduct(marta, 1L));
    }

    @Test
    void operatorDelegatesTheDispatch() {
        DispatchShipmentService dispatch = mock(DispatchShipmentService.class);
        LogisticsOperatorUseCaseImpl impl = new LogisticsOperatorUseCaseImpl(
                mock(RegisterInventoryEntryService.class), mock(StartShipmentPreparationService.class),
                mock(PackShipmentService.class), dispatch, mock(RegisterShipmentInTransitService.class),
                mock(ConfirmDeliveryService.class), mock(ReceiveReturnService.class));
        Shipment shipment = new Shipment(20L, 10L, "Street 1");
        when(dispatch.dispatch(oscar, 20L, "FastCarrier", "TR-1")).thenReturn(shipment);

        assertSame(shipment, impl.dispatchShipment(oscar, 20L, "FastCarrier", "TR-1"));
    }

    @Test
    void administratorDelegatesTheReturnApproval() {
        ApproveReturnService approve = mock(ApproveReturnService.class);
        AdministratorUseCaseImpl impl = new AdministratorUseCaseImpl(mock(RegisterSellerService.class),
                mock(CreateWarehouseService.class), approve, mock(RejectReturnService.class),
                mock(ProcessRefundService.class));
        OrderItem line = new OrderItem(1L, "T-shirt", ProductType.PHYSICAL, Money.of("35000"), 3, 7L);
        ReturnRequest request = new ReturnRequest(1L, 10L, 5L, List.of(ReturnItem.from(line, 1)), "Wrong size");
        when(approve.approve(admin, 1L)).thenReturn(request);

        assertSame(request, impl.approveReturn(admin, 1L));
    }
}