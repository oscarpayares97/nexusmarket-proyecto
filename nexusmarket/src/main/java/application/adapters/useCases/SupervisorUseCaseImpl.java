package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.SalesReport;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.SupervisorPort;
import application.domain.services.billing.ConsultInvoiceService;
import application.domain.services.billing.ConsultInvoicesService;
import application.domain.services.inventory.ConsultInventoryMovementsService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.report.GenerateSalesReportService;
import application.domain.services.returns.ConsultRefundsService;
import application.domain.services.returns.ConsultReturnsService;
import application.domain.services.shipping.ConsultShipmentService;
import application.domain.services.shipping.ConsultShipmentsService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.warehouse.ConsultWarehousesService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class SupervisorUseCaseImpl implements SupervisorPort {

    private final ConsultUserService consultUserService;
    private final ConsultWarehousesService consultWarehousesService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultInventoryMovementsService consultInventoryMovementsService;
    private final ConsultOrdersService consultOrdersService;
    private final ConsultInvoicesService consultInvoicesService;
    private final ConsultInvoiceService consultInvoiceService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ConsultShipmentService consultShipmentService;
    private final ConsultReturnsService consultReturnsService;
    private final ConsultRefundsService consultRefundsService;
    private final GenerateSalesReportService generateSalesReportService;
    private final ChangeUserPasswordService changeUserPasswordService;

    public SupervisorUseCaseImpl(ConsultUserService consultUserService,
                                 ConsultWarehousesService consultWarehousesService,
                                 ConsultInventoryService consultInventoryService,
                                 ConsultInventoryMovementsService consultInventoryMovementsService,
                                 ConsultOrdersService consultOrdersService,
                                 ConsultInvoicesService consultInvoicesService,
                                 ConsultInvoiceService consultInvoiceService,
                                 ConsultShipmentsService consultShipmentsService,
                                 ConsultShipmentService consultShipmentService,
                                 ConsultReturnsService consultReturnsService,
                                 ConsultRefundsService consultRefundsService,
                                 GenerateSalesReportService generateSalesReportService,
                                 ChangeUserPasswordService changeUserPasswordService) {
        this.consultUserService = consultUserService;
        this.consultWarehousesService = consultWarehousesService;
        this.consultInventoryService = consultInventoryService;
        this.consultInventoryMovementsService = consultInventoryMovementsService;
        this.consultOrdersService = consultOrdersService;
        this.consultInvoicesService = consultInvoicesService;
        this.consultInvoiceService = consultInvoiceService;
        this.consultShipmentsService = consultShipmentsService;
        this.consultShipmentService = consultShipmentService;
        this.consultReturnsService = consultReturnsService;
        this.consultRefundsService = consultRefundsService;
        this.generateSalesReportService = generateSalesReportService;
        this.changeUserPasswordService = changeUserPasswordService;
    }

    @Override
    public List<User> consultUsers(User actor) {
        return consultUserService.consultUsers(actor);
    }

    @Override
    public List<Warehouse> consultWarehouses(User actor) {
        return consultWarehousesService.consultWarehouses(actor);
    }

    @Override
    public List<Inventory> consultInventory(User actor, Long productId) {
        return consultInventoryService.consultInventory(actor, productId);
    }

    @Override
    public List<InventoryMovement> consultInventoryMovements(User actor) {
        return consultInventoryMovementsService.consultMovements(actor);
    }

    @Override
    public List<Order> consultOrders(User actor) {
        return consultOrdersService.consultOrders(actor);
    }

    @Override
    public List<Invoice> consultInvoices(User actor) {
        return consultInvoicesService.consultInvoices(actor);
    }

    @Override
    public Invoice consultInvoice(User actor, Long orderId) {
        return consultInvoiceService.consultInvoice(actor, orderId);
    }

    @Override
    public List<Shipment> consultShipments(User actor) {
        return consultShipmentsService.consultShipments(actor);
    }

    @Override
    public Shipment consultShipment(User actor, Long orderId) {
        return consultShipmentService.consultShipment(actor, orderId);
    }

    @Override
    public List<ReturnRequest> consultReturns(User actor) {
        return consultReturnsService.consultReturns(actor);
    }

    @Override
    public List<Refund> consultRefunds(User actor) {
        return consultRefundsService.consultRefunds(actor);
    }

    @Override
    public SalesReport generateSalesReport(User actor) {
        return generateSalesReportService.generate(actor);
    }

    @Override
    public void changePassword(User actor, String currentPassword, String newPassword) {
        changeUserPasswordService.change(actor, currentPassword, newPassword);
    }
}