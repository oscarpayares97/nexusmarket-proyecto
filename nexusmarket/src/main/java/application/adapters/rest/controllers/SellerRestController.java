package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.RequestDTOs.ChangePasswordRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.InventoryAdjustRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.InventoryEntryRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.InventoryStatusRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.RegisterProductRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.VariantRequest;
import application.adapters.rest.dtos.responses.ResponseDTOs.InventoryResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ProductResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.WarehouseResponse;
import application.adapters.rest.mappers.DomainRestMapper;
import application.domain.models.User;
import application.domain.ports.in.SellerPort;
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

/** Operaciones del rol SELLER: catálogo propio e inventario. */
@RestController
@RequestMapping("/api/v1/seller")
public class SellerRestController {

    private final SellerPort sellerPort;

    public SellerRestController(SellerPort sellerPort) {
        this.sellerPort = sellerPort;
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse registerProduct(@AuthenticationPrincipal(expression = "user") User actor,
                                           @Valid @RequestBody RegisterProductRequest request) {
        SellerPort.RegisterProductCommand command = new SellerPort.RegisterProductCommand(
                request.name(), request.type(), request.price());
        return DomainRestMapper.toProduct(sellerPort.registerProduct(actor, command));
    }

    @PostMapping("/products/{productId}/publish")
    public ProductResponse publishProduct(@AuthenticationPrincipal(expression = "user") User actor,
                                          @PathVariable Long productId) {
        return DomainRestMapper.toProduct(sellerPort.publishProduct(actor, productId));
    }

    @PostMapping("/products/{productId}/variants")
    public ProductResponse addProductVariant(@AuthenticationPrincipal(expression = "user") User actor,
                                             @PathVariable Long productId,
                                             @Valid @RequestBody VariantRequest request) {
        return DomainRestMapper.toProduct(
                sellerPort.addProductVariant(actor, productId, request.variant()));
    }

    @PostMapping("/products/{productId}/suspend")
    public ProductResponse suspendProduct(@AuthenticationPrincipal(expression = "user") User actor,
                                          @PathVariable Long productId) {
        return DomainRestMapper.toProduct(sellerPort.suspendProduct(actor, productId));
    }

    @PostMapping("/products/{productId}/discontinue")
    public ProductResponse discontinueProduct(@AuthenticationPrincipal(expression = "user") User actor,
                                              @PathVariable Long productId) {
        return DomainRestMapper.toProduct(sellerPort.discontinueProduct(actor, productId));
    }

    @PostMapping("/inventory/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse registerInventoryEntry(@AuthenticationPrincipal(expression = "user") User actor,
                                                    @Valid @RequestBody InventoryEntryRequest request) {
        return DomainRestMapper.toInventory(sellerPort.registerInventoryEntry(
                actor, request.productId(), request.warehouseId(), request.quantity()));
    }

    @PutMapping("/inventory/adjustment")
    public InventoryResponse adjustInventory(@AuthenticationPrincipal(expression = "user") User actor,
                                             @Valid @RequestBody InventoryAdjustRequest request) {
        return DomainRestMapper.toInventory(sellerPort.adjustInventory(
                actor, request.productId(), request.warehouseId(), request.newQuantity()));
    }

    @PutMapping("/inventory/status")
    public InventoryResponse changeInventoryStatus(@AuthenticationPrincipal(expression = "user") User actor,
                                                   @Valid @RequestBody InventoryStatusRequest request) {
        return DomainRestMapper.toInventory(sellerPort.changeInventoryStatus(
                actor, request.productId(), request.warehouseId(), request.status()));
    }

    @GetMapping("/inventory/product/{productId}")
    public List<InventoryResponse> consultInventory(@AuthenticationPrincipal(expression = "user") User actor,
                                                    @PathVariable Long productId) {
        return DomainRestMapper.mapList(sellerPort.consultInventory(actor, productId),
                DomainRestMapper::toInventory);
    }

    @GetMapping("/warehouses")
    public List<WarehouseResponse> consultWarehouses(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(sellerPort.consultWarehouses(actor),
                DomainRestMapper::toWarehouse);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal(expression = "user") User actor,
                               @Valid @RequestBody ChangePasswordRequest request) {
        sellerPort.changePassword(actor, request.currentPassword(), request.newPassword());
    }
}