package application.adapters.useCases;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.SalesReport;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.PublicAccessPort;
import application.domain.ports.in.SellerPort;
import application.domain.services.billing.ConsultInvoicesService;
import application.domain.services.buyer.ChangeBuyerCommercialStatusService;
import application.domain.services.buyer.RegisterBuyerService;
import application.domain.services.catalog.ConsultPublishedProductsService;
import application.domain.services.catalog.RegisterProductService;
import application.domain.services.catalog.SuspendProductService;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.order.AddItemToCartService;
import application.domain.services.order.ConsultMyOrdersService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.order.RemoveItemFromCartService;
import application.domain.services.report.GenerateSalesReportService;
import application.domain.services.returns.ConsultRefundsService;
import application.domain.services.shipping.ConsultShipmentsService;
import application.domain.services.shipping.DispatchShipmentService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.LoginService;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Cada caso de uso solo delega en un servicio del dominio: aquí se comprueba esa delegación. */
@ExtendWith(MockitoExtension.class)
class UseCaseImplDelegationTest {

    private static User actor() {
        return new User(1L, "DOC-1", "Actor Test", "actor@test.com", "hash", UserRole.ADMINISTRATOR);
    }

    @Nested
    class PublicAccess {
        @Mock RegisterBuyerService registerBuyerService;
        @Mock LoginService loginService;
        @Mock ConsultPublishedProductsService consultPublishedProductsService;
        @InjectMocks PublicAccessUseCaseImpl useCase;

        @Test
        void registerBuyerDelegates() {
            PublicAccessPort.RegisterBuyerCommand command =
                    new PublicAccessPort.RegisterBuyerCommand("D1", "Ana", "ana@test.com", "Clave12345", "Calle 1");
            Buyer buyer = mock(Buyer.class);
            when(registerBuyerService.register(command)).thenReturn(buyer);

            assertSame(buyer, useCase.registerBuyer(command));
        }

        @Test
        void consultPublishedProductsDelegates() {
            List<Product> products = List.of(mock(Product.class));
            when(consultPublishedProductsService.consult()).thenReturn(products);

            assertSame(products, useCase.consultPublishedProducts());
        }
    }

    @Nested
    class BuyerRole {
        @Mock AddItemToCartService addItemToCartService;
        @Mock RemoveItemFromCartService removeItemFromCartService;
        @Mock ConsultMyOrdersService consultMyOrdersService;
        @Mock ChangeUserPasswordService changeUserPasswordService;
        @InjectMocks BuyerUseCaseImpl useCase;

        @Test
        void addItemToCartDelegates() {
            User actor = actor();
            Order order = mock(Order.class);
            when(addItemToCartService.add(actor, 5L, 2)).thenReturn(order);

            assertSame(order, useCase.addItemToCart(actor, 5L, 2));
        }

        @Test
        void removeItemFromCartDelegates() {
            User actor = actor();
            Order order = mock(Order.class);
            when(removeItemFromCartService.remove(actor, 5L)).thenReturn(order);

            assertSame(order, useCase.removeItemFromCart(actor, 5L));
        }

        @Test
        void consultMyOrdersDelegates() {
            User actor = actor();
            List<Order> orders = List.of(mock(Order.class));
            when(consultMyOrdersService.consultMyOrders(actor)).thenReturn(orders);

            assertSame(orders, useCase.consultMyOrders(actor));
        }

        @Test
        void changePasswordDelegates() {
            User actor = actor();

            useCase.changePassword(actor, "old", "new");

            verify(changeUserPasswordService).change(actor, "old", "new");
        }
    }

    @Nested
    class SellerRole {
        @Mock RegisterProductService registerProductService;
        @Mock SuspendProductService suspendProductService;
        @Mock ConsultInventoryService consultInventoryService;
        @InjectMocks SellerUseCaseImpl useCase;

        @Test
        void registerProductDelegates() {
            User actor = actor();
            SellerPort.RegisterProductCommand command =
                    new SellerPort.RegisterProductCommand("Mouse", ProductType.PHYSICAL, new BigDecimal("50000"));
            Product product = mock(Product.class);
            when(registerProductService.register(actor, command)).thenReturn(product);

            assertSame(product, useCase.registerProduct(actor, command));
        }

        @Test
        void suspendProductDelegates() {
            User actor = actor();
            Product product = mock(Product.class);
            when(suspendProductService.suspend(actor, 7L)).thenReturn(product);

            assertSame(product, useCase.suspendProduct(actor, 7L));
        }

        @Test
        void consultInventoryDelegates() {
            User actor = actor();
            List<Inventory> inventory = List.of(mock(Inventory.class));
            when(consultInventoryService.consultInventory(actor, 7L)).thenReturn(inventory);

            assertSame(inventory, useCase.consultInventory(actor, 7L));
        }
    }

    @Nested
    class LogisticsRole {
        @Mock DispatchShipmentService dispatchShipmentService;
        @Mock ConsultShipmentsService consultShipmentsService;
        @Mock AdjustInventoryService adjustInventoryService;
        @InjectMocks LogisticsOperatorUseCaseImpl useCase;

        @Test
        void dispatchShipmentDelegates() {
            User actor = actor();
            Shipment shipment = mock(Shipment.class);
            when(dispatchShipmentService.dispatch(actor, 3L, "Servientrega", "TRK-1")).thenReturn(shipment);

            assertSame(shipment, useCase.dispatchShipment(actor, 3L, "Servientrega", "TRK-1"));
        }

        @Test
        void consultShipmentsDelegates() {
            User actor = actor();
            List<Shipment> shipments = List.of(mock(Shipment.class));
            when(consultShipmentsService.consultShipments(actor)).thenReturn(shipments);

            assertSame(shipments, useCase.consultShipments(actor));
        }

        @Test
        void adjustInventoryDelegates() {
            User actor = actor();
            Inventory inventory = mock(Inventory.class);
            when(adjustInventoryService.adjust(actor, 1L, 2L, 30)).thenReturn(inventory);

            assertSame(inventory, useCase.adjustInventory(actor, 1L, 2L, 30));
        }
    }

    @Nested
    class AdministratorRole {
        @Mock ChangeBuyerCommercialStatusService changeBuyerCommercialStatusService;
        @Mock GenerateSalesReportService generateSalesReportService;
        @Mock ConsultRefundsService consultRefundsService;
        @InjectMocks AdministratorUseCaseImpl useCase;

        @Test
        void changeBuyerCommercialStatusDelegates() {
            User actor = actor();
            Buyer buyer = mock(Buyer.class);
            when(changeBuyerCommercialStatusService.change(actor, 4L, CommercialStatus.SUSPENDED))
                    .thenReturn(buyer);

            assertSame(buyer, useCase.changeBuyerCommercialStatus(actor, 4L, CommercialStatus.SUSPENDED));
        }

        @Test
        void generateSalesReportDelegates() {
            User actor = actor();
            SalesReport report = mock(SalesReport.class);
            when(generateSalesReportService.generate(actor)).thenReturn(report);

            assertSame(report, useCase.generateSalesReport(actor));
        }

        @Test
        void consultRefundsDelegates() {
            User actor = actor();
            List<Refund> refunds = List.of(mock(Refund.class));
            when(consultRefundsService.consultRefunds(actor)).thenReturn(refunds);

            assertSame(refunds, useCase.consultRefunds(actor));
        }
    }

    @Nested
    class SupervisorRole {
        @Mock ConsultUserService consultUserService;
        @Mock ConsultOrdersService consultOrdersService;
        @Mock ConsultInvoicesService consultInvoicesService;
        @Mock GenerateSalesReportService generateSalesReportService;
        @InjectMocks SupervisorUseCaseImpl useCase;

        @Test
        void consultUsersDelegates() {
            User actor = actor();
            List<User> users = List.of(actor);
            when(consultUserService.consultUsers(actor)).thenReturn(users);

            assertSame(users, useCase.consultUsers(actor));
        }

        @Test
        void consultOrdersDelegates() {
            User actor = actor();
            List<Order> orders = List.of(mock(Order.class));
            when(consultOrdersService.consultOrders(actor)).thenReturn(orders);

            assertSame(orders, useCase.consultOrders(actor));
        }

        @Test
        void generateSalesReportDelegates() {
            User actor = actor();
            SalesReport report = mock(SalesReport.class);
            when(generateSalesReportService.generate(actor)).thenReturn(report);

            assertSame(report, useCase.generateSalesReport(actor));
        }
    }
}