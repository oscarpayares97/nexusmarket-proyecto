package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.valueobjects.MovementType;

import java.time.LocalDateTime;

public class InventoryMovement {

    private Long id;
    private final MovementType type;
    private final Long productId;
    private final Long warehouseId;
    private final int quantity;
    private final Long userId;
    private final LocalDateTime occurredAt;

    public InventoryMovement(Long id, MovementType type, Long productId,
                             Long warehouseId, int quantity, Long userId) {
        this(id, type, productId, warehouseId, quantity, userId, LocalDateTime.now());
    }

    public InventoryMovement(Long id, MovementType type, Long productId, Long warehouseId,
                             int quantity, Long userId, LocalDateTime occurredAt) {
        Guard.requireNonNull(type, "El tipo de movimiento");
        Guard.requireNonNull(productId, "El producto");
        Guard.requireNonNull(warehouseId, "El almacen");
        Guard.requireNonNull(userId, "El usuario");
        Guard.requireNonNull(occurredAt, "La fecha");
        if (quantity < 0) {
            throw new InvalidDataException("La cantidad no puede ser negativa.");
        }
        this.id = id;
        this.type = type;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.userId = userId;
        this.occurredAt = occurredAt;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("El movimiento ya tiene un identificador.");
        }
        this.id = id;
    }

    public Long getId() { return id; }
    public MovementType getType() { return type; }
    public Long getProductId() { return productId; }
    public Long getWarehouseId() { return warehouseId; }
    public int getQuantity() { return quantity; }
    public Long getUserId() { return userId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}