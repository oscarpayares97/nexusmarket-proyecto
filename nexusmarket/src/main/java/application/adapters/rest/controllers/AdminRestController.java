package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.RequestDTOs.ChangePasswordRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.CommercialStatusRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.CreateWarehouseRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.RegisterInternalUserRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.RegisterSellerRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.RejectReturnRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.UserStatusRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.WarehouseStatusRequest;
import application.adapters.rest.dtos.responses.ResponseDTOs.BuyerProfileResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.InventoryResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.InvoiceResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.MovementResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.OrderResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.RefundResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ReturnResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.SalesReportResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.SellerResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ShipmentResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.UserResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.WarehouseResponse;
import application.adapters.rest.mappers.DomainRestMapper;
import application.domain.models.User;
import application.domain.ports.in.AdministratorPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Operaciones del rol ADMINISTRATOR: altas, estados, devoluciones, reembolsos y reportes. */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    private final AdministratorPort adminPort;

    public AdminRestController(AdministratorPort adminPort) {
        this.adminPort = adminPort;
    }

    // ----- Altas y estados -----
    @PostMapping("/sellers")
    @ResponseStatus(HttpStatus.CREATED)
    public SellerResponse registerSeller(@AuthenticationPrincipal(expression = "user") User actor,
                                         @Valid @RequestBody RegisterSellerRequest request) {
        AdministratorPort.RegisterSellerCommand command = new AdministratorPort.RegisterSellerCommand(
                request.document(), request.fullName(), request.email(), request.rawPassword(),
                request.tradeName(), request.warehouseName(), request.warehouseLocation());
        return DomainRestMapper.toSeller(adminPort.registerSeller(actor, command));
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerInternalUser(@AuthenticationPrincipal(expression = "user") User actor,
                                             @Valid @RequestBody RegisterInternalUserRequest request) {
        AdministratorPort.RegisterInternalUserCommand command =
                new AdministratorPort.RegisterInternalUserCommand(request.document(), request.fullName(),
                        request.email(), request.rawPassword(), request.role());
        return DomainRestMapper.toUser(adminPort.registerInternalUser(actor, command));
    }

    @PutMapping("/users/{userId}/status")
    public UserResponse changeUserStatus(@AuthenticationPrincipal(expression = "user") User actor,
                                         @PathVariable Long userId,
                                         @Valid @RequestBody UserStatusRequest request) {
        return DomainRestMapper.toUser(adminPort.changeUserStatus(actor, userId, request.status()));
    }

    @PutMapping("/buyers/{buyerUserId}/commercial-status")
    public BuyerProfileResponse changeBuyerCommercialStatus(@AuthenticationPrincipal(expression = "user") User actor,
                                                            @PathVariable Long buyerUserId,
                                                            @Valid @RequestBody CommercialStatusRequest request) {
        return DomainRestMapper.toBuyer(
                adminPort.changeBuyerCommercialStatus(actor, buyerUserId, request.status()));
    }

    @PostMapping("/warehouses")
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponse createWarehouse(@AuthenticationPrincipal(expression = "user") User actor,
                                             @Valid @RequestBody CreateWarehouseRequest request) {
        AdministratorPort.CreateWarehouseCommand command = new AdministratorPort.CreateWarehouseCommand(
                request.name(), request.location(), request.sellerId());
        return DomainRestMapper.toWarehouse(adminPort.createWarehouse(actor, command));
    }

    @PutMapping("/warehouses/{warehouseId}/status")
    public WarehouseResponse changeWarehouseStatus(@AuthenticationPrincipal(expression = "user") User actor,
                                                   @PathVariable Long warehouseId,
                                                   @Valid @RequestBody WarehouseStatusRequest request) {
        return DomainRestMapper.toWarehouse(
                adminPort.changeWarehouseStatus(actor, warehouseId, request.active()));
    }

    // ----- Devoluciones y reembolsos -----
    @PostMapping("/returns/{returnId}/approve")
    public ReturnResponse approveReturn(@AuthenticationPrincipal(expression = "user") User actor,
                                        @PathVariable Long returnId) {
        return DomainRestMapper.toReturn(adminPort.approveReturn(actor, returnId));
    }

    @PostMapping("/returns/{returnId}/reject")
    public ReturnResponse rejectReturn(@AuthenticationPrincipal(expression = "user") User actor,
                                       @PathVariable Long returnId,
                                       @Valid @RequestBody RejectReturnRequest request) {
        return DomainRestMapper.toReturn(adminPort.rejectReturn(actor, returnId, request.reason()));
    }

    @PostMapping("/refunds/{refundId}/process")
    public RefundResponse processRefund(@AuthenticationPrincipal(expression = "user") User actor,
                                        @PathVariable Long refundId) {
        return DomainRestMapper.toRefund(adminPort.processRefund(actor, refundId));
    }

    // ----- Consultas y reportes -----
    @GetMapping("/users")
    public List<UserResponse> consultUsers(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(adminPort.consultUsers(actor), DomainRestMapper::toUser);
    }

    @GetMapping("/warehouses")
    public List<WarehouseResponse> consultWarehouses(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(adminPort.consultWarehouses(actor), DomainRestMapper::toWarehouse);
    }

    @GetMapping("/inventory/product/{productId}")
    public List<InventoryResponse> consultInventory(@AuthenticationPrincipal(expression = "user") User actor,
                                                    @PathVariable Long productId) {
        return DomainRestMapper.mapList(adminPort.consultInventory(actor, productId),
                DomainRestMapper::toInventory);
    }

    @GetMapping("/inventory/movements")
    public List<MovementResponse> consultInventoryMovements(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(adminPort.consultInventoryMovements(actor),
                DomainRestMapper::toMovement);
    }

    @GetMapping("/orders")
    public List<OrderResponse> consultOrders(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(adminPort.consultOrders(actor), DomainRestMapper::toOrder);
    }

    @GetMapping("/invoices")
    public List<InvoiceResponse> consultInvoices(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(adminPort.consultInvoices(actor), DomainRestMapper::toInvoice);
    }

    @GetMapping("/orders/{orderId}/invoice")
    public InvoiceResponse consultInvoice(@AuthenticationPrincipal(expression = "user") User actor,
                                          @PathVariable Long orderId) {
        return DomainRestMapper.toInvoice(adminPort.consultInvoice(actor, orderId));
    }

    @GetMapping("/shipments")
    public List<ShipmentResponse> consultShipments(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(adminPort.consultShipments(actor), DomainRestMapper::toShipment);
    }

    @GetMapping("/orders/{orderId}/shipment")
    public ShipmentResponse consultShipment(@AuthenticationPrincipal(expression = "user") User actor,
                                            @PathVariable Long orderId) {
        return DomainRestMapper.toShipment(adminPort.consultShipment(actor, orderId));
    }

    @GetMapping("/returns")
    public List<ReturnResponse> consultReturns(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(adminPort.consultReturns(actor), DomainRestMapper::toReturn);
    }

    @GetMapping("/refunds")
    public List<RefundResponse> consultRefunds(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(adminPort.consultRefunds(actor), DomainRestMapper::toRefund);
    }

    @GetMapping("/reports/sales")
    public SalesReportResponse generateSalesReport(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.toSalesReport(adminPort.generateSalesReport(actor));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal(expression = "user") User actor,
                               @Valid @RequestBody ChangePasswordRequest request) {
        adminPort.changePassword(actor, request.currentPassword(), request.newPassword());
    }
}