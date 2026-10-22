package application.adapters.useCases;

import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.SalesReport;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort;
import application.domain.services.billing.ConsultInvoiceService;
import application.domain.services.billing.ConsultInvoicesService;
import application.domain.services.buyer.ChangeBuyerCommercialStatusService;
import application.domain.services.inventory.ConsultInventoryMovementsService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.order.ConsultOrdersService;
import application.domain.services.report.GenerateSalesReportService;
import application.domain.services.returns.ApproveReturnService;
import application.domain.services.returns.ConsultRefundsService;
import application.domain.services.returns.ConsultReturnsService;
import application.domain.services.returns.ProcessRefundService;
import application.domain.services.returns.RejectReturnService;
import application.domain.services.seller.RegisterSellerService;
import application.domain.services.shipping.ConsultShipmentService;
import application.domain.services.shipping.ConsultShipmentsService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.user.ChangeUserStatusService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.RegisterInternalUserService;
import application.domain.services.warehouse.ChangeWarehouseStatusService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.services.warehouse.CreateWarehouseService;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.UserStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class AdministratorUseCaseImpl implements AdministratorPort {

    private final RegisterSellerService registerSellerService;
    private final RegisterInternalUserService registerInternalUserService;
    private final ChangeUserStatusService changeUserStatusService;
    private final ChangeBuyerCommercialStatusService changeBuyerCommercialStatusService;
    private final CreateWarehouseService createWarehouseService;
    private final ChangeWarehouseStatusService changeWarehouseStatusService;
    private final ApproveReturnService approveReturnService;
    private final RejectReturnService rejectReturnService;
    private final ProcessRefundService processRefundService;
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

    public AdministratorUseCaseImpl(RegisterSellerService registerSellerService,
                                    RegisterInternalUserService registerInternalUserService,
                                    ChangeUserStatusService changeUserStatusService,
                                    ChangeBuyerCommercialStatusService changeBuyerCommercialStatusService,
                                    CreateWarehouseService createWarehouseService,
                                    ChangeWarehouseStatusService changeWarehouseStatusService,
                                    ApproveReturnService approveReturnService,
                                    RejectReturnService rejectReturnService,
                                    ProcessRefundService processRefundService,
                                    ConsultUserService consultUserService,
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
        this.registerSellerService = registerSellerService;
        this.registerInternalUserService = registerInternalUserService;
        this.changeUserStatusService = changeUserStatusService;
        this.changeBuyerCommercialStatusService = changeBuyerCommercialStatusService;
        this.createWarehouseService = createWarehouseService;
        this.changeWarehouseStatusService = changeWarehouseStatusService;
        this.approveReturnService = approveReturnService;
        this.rejectReturnService = rejectReturnService;
        this.processRefundService = processRefundService;
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
    public Seller registerSeller(User actor, RegisterSellerCommand command) {
        return registerSellerService.register(actor, command);
    }

    @Override
    public User registerInternalUser(User actor, RegisterInternalUserCommand command) {
        return registerInternalUserService.register(actor, command);
    }

    @Override
    public User changeUserStatus(User actor, Long userId, UserStatus newStatus) {
        return changeUserStatusService.change(actor, userId, newStatus);
    }

    @Override
    public Buyer changeBuyerCommercialStatus(User actor, Long buyerUserId, CommercialStatus newStatus) {
        return changeBuyerCommercialStatusService.change(actor, buyerUserId, newStatus);
    }

    @Override
    public Warehouse createWarehouse(User actor, CreateWarehouseCommand command) {
        return createWarehouseService.create(actor, command);
    }

    @Override
    public Warehouse changeWarehouseStatus(User actor, Long warehouseId, boolean active) {
        return changeWarehouseStatusService.change(actor, warehouseId, active);
    }

    @Override
    public ReturnRequest approveReturn(User actor, Long returnId) {
        return approveReturnService.approve(actor, returnId);
    }

    @Override
    public ReturnRequest rejectReturn(User actor, Long returnId, String reason) {
        return rejectReturnService.reject(actor, returnId, reason);
    }

    @Override
    public Refund processRefund(User actor, Long refundId) {
        return processRefundService.process(actor, refundId);
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