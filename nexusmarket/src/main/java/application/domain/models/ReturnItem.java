package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.valueobjects.Money;

public class ReturnItem {

    private final Long productId;
    private final String productName;
    private final int quantity;
    private final Money unitPrice;
    private final Long warehouseId;

    public ReturnItem(Long productId, String productName, int quantity,
                      Money unitPrice, Long warehouseId) {
        Guard.requireNonNull(productId, "El producto");
        Guard.requirePositive(quantity, "La cantidad");
        Guard.requireNonNull(unitPrice, "Precio de la unidad");
        Guard.requireNonNull(warehouseId, "El almacen");
        this.productId = productId;
        this.productName = Guard.requireText(productName, "El nombre del producto");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.warehouseId = warehouseId;
    }

    public static ReturnItem from(OrderItem item, int quantity) {
        if (!item.isPhysical()) {
            throw new InvalidDataException("Los productos digitales no se pueden devolver.");
        }
        if (quantity > item.getQuantity()) {
            throw new InvalidDataException("No se pueden devolver más unidades de las que se compraron.");
        }
        return new ReturnItem(item.getProductId(), item.getProductName(), quantity,
                item.getUnitPrice(), item.getWarehouseId());
    }

    public Money subtotal() { return unitPrice.multiply(quantity); }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public Money getUnitPrice() { return unitPrice; }
    public Long getWarehouseId() { return warehouseId; }
}