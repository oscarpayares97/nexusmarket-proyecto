package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.RequestDTOs.ChangePasswordRequest;
import application.adapters.rest.dtos.responses.ResponseDTOs.InventoryResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.InvoiceResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.MovementResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.OrderResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.RefundResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ReturnResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.SalesReportResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ShipmentResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.UserResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.WarehouseResponse;
import application.adapters.rest.mappers.DomainRestMapper;
import application.domain.models.User;
import application.domain.ports.in.SupervisorPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Rol SUPERVISOR: solo consulta y reportes (más cambiar su propia contraseña). */
@RestController
@RequestMapping("/api/v1/supervisor")
public class SupervisorRestController {

    private final SupervisorPort supervisorPort;

    public SupervisorRestController(SupervisorPort supervisorPort) {
        this.supervisorPort = supervisorPort;
    }

    @GetMapping("/users")
    public List<UserResponse> consultUsers(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(supervisorPort.consultUsers(actor), DomainRestMapper::toUser);
    }

    @GetMapping("/warehouses")
    public List<WarehouseResponse> consultWarehouses(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(supervisorPort.consultWarehouses(actor), DomainRestMapper::toWarehouse);
    }

    @GetMapping("/inventory/product/{productId}")
    public List<InventoryResponse> consultInventory(@AuthenticationPrincipal(expression = "user") User actor,
                                                    @PathVariable Long productId) {
        return DomainRestMapper.mapList(supervisorPort.consultInventory(actor, productId),
                DomainRestMapper::toInventory);
    }

    @GetMapping("/inventory/movements")
    public List<MovementResponse> consultInventoryMovements(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(supervisorPort.consultInventoryMovements(actor),
                DomainRestMapper::toMovement);
    }

    @GetMapping("/orders")
    public List<OrderResponse> consultOrders(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(supervisorPort.consultOrders(actor), DomainRestMapper::toOrder);
    }

    @GetMapping("/invoices")
    public List<InvoiceResponse> consultInvoices(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(supervisorPort.consultInvoices(actor), DomainRestMapper::toInvoice);
    }

    @GetMapping("/orders/{orderId}/invoice")
    public InvoiceResponse consultInvoice(@AuthenticationPrincipal(expression = "user") User actor,
                                          @PathVariable Long orderId) {
        return DomainRestMapper.toInvoice(supervisorPort.consultInvoice(actor, orderId));
    }

    @GetMapping("/shipments")
    public List<ShipmentResponse> consultShipments(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(supervisorPort.consultShipments(actor), DomainRestMapper::toShipment);
    }

    @GetMapping("/orders/{orderId}/shipment")
    public ShipmentResponse consultShipment(@AuthenticationPrincipal(expression = "user") User actor,
                                            @PathVariable Long orderId) {
        return DomainRestMapper.toShipment(supervisorPort.consultShipment(actor, orderId));
    }

    @GetMapping("/returns")
    public List<ReturnResponse> consultReturns(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(supervisorPort.consultReturns(actor), DomainRestMapper::toReturn);
    }

    @GetMapping("/refunds")
    public List<RefundResponse> consultRefunds(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(supervisorPort.consultRefunds(actor), DomainRestMapper::toRefund);
    }

    @GetMapping("/reports/sales")
    public SalesReportResponse generateSalesReport(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.toSalesReport(supervisorPort.generateSalesReport(actor));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal(expression = "user") User actor,
                               @Valid @RequestBody ChangePasswordRequest request) {
        supervisorPort.changePassword(actor, request.currentPassword(), request.newPassword());
    }
}