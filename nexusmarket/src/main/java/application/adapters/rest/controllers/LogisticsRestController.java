package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.RequestDTOs.ChangePasswordRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.DispatchRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.InventoryAdjustRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.InventoryEntryRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.InventoryStatusRequest;
import application.adapters.rest.dtos.responses.ResponseDTOs.InventoryResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.MovementResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.OrderResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ReturnResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ShipmentResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.WarehouseResponse;
import application.adapters.rest.mappers.DomainRestMapper;
import application.domain.models.User;
import application.domain.ports.in.LogisticsOperatorPort;
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

/** Operaciones del rol LOGISTICS_OPERATOR: envíos, devoluciones recibidas e inventario. */
@RestController
@RequestMapping("/api/v1/logistics")
public class LogisticsRestController {

    private final LogisticsOperatorPort logisticsPort;

    public LogisticsRestController(LogisticsOperatorPort logisticsPort) {
        this.logisticsPort = logisticsPort;
    }

    // ----- Inventario -----
    @PostMapping("/inventory/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse registerInventoryEntry(@AuthenticationPrincipal(expression = "user") User actor,
                                                    @Valid @RequestBody InventoryEntryRequest request) {
        return DomainRestMapper.toInventory(logisticsPort.registerInventoryEntry(
                actor, request.productId(), request.warehouseId(), request.quantity()));
    }

    @PutMapping("/inventory/adjustment")
    public InventoryResponse adjustInventory(@AuthenticationPrincipal(expression = "user") User actor,
                                             @Valid @RequestBody InventoryAdjustRequest request) {
        return DomainRestMapper.toInventory(logisticsPort.adjustInventory(
                actor, request.productId(), request.warehouseId(), request.newQuantity()));
    }

    @PutMapping("/inventory/status")
    public InventoryResponse changeInventoryStatus(@AuthenticationPrincipal(expression = "user") User actor,
                                                   @Valid @RequestBody InventoryStatusRequest request) {
        return DomainRestMapper.toInventory(logisticsPort.changeInventoryStatus(
                actor, request.productId(), request.warehouseId(), request.status()));
    }

    @GetMapping("/inventory/product/{productId}")
    public List<InventoryResponse> consultInventory(@AuthenticationPrincipal(expression = "user") User actor,
                                                    @PathVariable Long productId) {
        return DomainRestMapper.mapList(logisticsPort.consultInventory(actor, productId),
                DomainRestMapper::toInventory);
    }

    @GetMapping("/inventory/movements")
    public List<MovementResponse> consultInventoryMovements(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(logisticsPort.consultInventoryMovements(actor),
                DomainRestMapper::toMovement);
    }

    @GetMapping("/warehouses")
    public List<WarehouseResponse> consultWarehouses(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(logisticsPort.consultWarehouses(actor),
                DomainRestMapper::toWarehouse);
    }

    // ----- Ciclo del envío -----
    @PostMapping("/orders/{orderId}/shipment/prepare")
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentResponse startShipmentPreparation(@AuthenticationPrincipal(expression = "user") User actor,
                                                     @PathVariable Long orderId) {
        return DomainRestMapper.toShipment(logisticsPort.startShipmentPreparation(actor, orderId));
    }

    @PostMapping("/shipments/{shipmentId}/pack")
    public ShipmentResponse packShipment(@AuthenticationPrincipal(expression = "user") User actor,
                                         @PathVariable Long shipmentId) {
        return DomainRestMapper.toShipment(logisticsPort.packShipment(actor, shipmentId));
    }

    @PostMapping("/shipments/{shipmentId}/dispatch")
    public ShipmentResponse dispatchShipment(@AuthenticationPrincipal(expression = "user") User actor,
                                             @PathVariable Long shipmentId,
                                             @Valid @RequestBody DispatchRequest request) {
        return DomainRestMapper.toShipment(logisticsPort.dispatchShipment(
                actor, shipmentId, request.carrier(), request.trackingNumber()));
    }

    @PostMapping("/shipments/{shipmentId}/in-transit")
    public ShipmentResponse registerShipmentInTransit(@AuthenticationPrincipal(expression = "user") User actor,
                                                      @PathVariable Long shipmentId) {
        return DomainRestMapper.toShipment(logisticsPort.registerShipmentInTransit(actor, shipmentId));
    }

    @PostMapping("/shipments/{shipmentId}/deliver")
    public ShipmentResponse confirmDelivery(@AuthenticationPrincipal(expression = "user") User actor,
                                            @PathVariable Long shipmentId) {
        return DomainRestMapper.toShipment(logisticsPort.confirmDelivery(actor, shipmentId));
    }

    // ----- Devoluciones -----
    @PostMapping("/returns/{returnId}/receive")
    public ReturnResponse receiveReturn(@AuthenticationPrincipal(expression = "user") User actor,
                                        @PathVariable Long returnId) {
        return DomainRestMapper.toReturn(logisticsPort.receiveReturn(actor, returnId));
    }

    // ----- Consultas -----
    @GetMapping("/orders")
    public List<OrderResponse> consultOrders(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(logisticsPort.consultOrders(actor), DomainRestMapper::toOrder);
    }

    @GetMapping("/shipments")
    public List<ShipmentResponse> consultShipments(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(logisticsPort.consultShipments(actor), DomainRestMapper::toShipment);
    }

    @GetMapping("/orders/{orderId}/shipment")
    public ShipmentResponse consultShipment(@AuthenticationPrincipal(expression = "user") User actor,
                                            @PathVariable Long orderId) {
        return DomainRestMapper.toShipment(logisticsPort.consultShipment(actor, orderId));
    }

    @GetMapping("/returns")
    public List<ReturnResponse> consultReturns(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(logisticsPort.consultReturns(actor), DomainRestMapper::toReturn);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal(expression = "user") User actor,
                               @Valid @RequestBody ChangePasswordRequest request) {
        logisticsPort.changePassword(actor, request.currentPassword(), request.newPassword());
    }
}