package application.adapters.rest.dtos.requests;

import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

/** Cuerpos JSON que reciben los endpoints. Cada record es un DTO inmutable con sus validaciones. */
public final class RequestDTOs {

    private RequestDTOs() {
    }

    // ----- Comprador -----
    public record AddressRequest(@NotBlank String address) {
    }

    public record AddCartItemRequest(@NotNull Long productId, @Min(1) int quantity) {
    }

    public record ConfirmOrderRequest(@NotBlank String deliveryAddress) {
    }

    public record PayOrderRequest(boolean paymentApproved) {
    }

    public record RequestReturnRequest(@NotNull Long orderId,
                                       @NotEmpty Map<Long, Integer> quantities,
                                       @NotBlank String reason) {
    }

    // ----- Todos los roles -----
    public record ChangePasswordRequest(@NotBlank String currentPassword,
                                        @NotBlank String newPassword) {
    }

    // ----- Vendedor -----
    public record RegisterProductRequest(@NotBlank String name,
                                         @NotNull ProductType type,
                                         @NotNull @DecimalMin("0.01") BigDecimal price) {
    }

    public record VariantRequest(@NotBlank String variant) {
    }

    // ----- Inventario (vendedor y operador logístico) -----
    public record InventoryEntryRequest(@NotNull Long productId, @NotNull Long warehouseId,
                                        @Min(1) int quantity) {
    }

    public record InventoryAdjustRequest(@NotNull Long productId, @NotNull Long warehouseId,
                                         @Min(0) int newQuantity) {
    }

    public record InventoryStatusRequest(@NotNull Long productId, @NotNull Long warehouseId,
                                         @NotNull InventoryStatus status) {
    }

    // ----- Operador logístico -----
    public record DispatchRequest(@NotBlank String carrier, @NotBlank String trackingNumber) {
    }

    // ----- Administrador -----
    public record RegisterSellerRequest(@NotBlank String document, @NotBlank String fullName,
                                        @NotBlank String email, @NotBlank String rawPassword,
                                        @NotBlank String tradeName, @NotBlank String warehouseName,
                                        @NotBlank String warehouseLocation) {
    }

    public record RegisterInternalUserRequest(@NotBlank String document, @NotBlank String fullName,
                                              @NotBlank String email, @NotBlank String rawPassword,
                                              @NotNull UserRole role) {
    }

    public record CreateWarehouseRequest(@NotBlank String name, @NotBlank String location,
                                         Long sellerId) {
    }

    public record UserStatusRequest(@NotNull UserStatus status) {
    }

    public record CommercialStatusRequest(@NotNull CommercialStatus status) {
    }

    public record WarehouseStatusRequest(boolean active) {
    }

    public record RejectReturnRequest(@NotBlank String reason) {
    }
}