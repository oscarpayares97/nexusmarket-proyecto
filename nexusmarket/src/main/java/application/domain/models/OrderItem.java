package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;

public class OrderItem {

    private final Long productId;
    private final String productName;
    private final ProductType productType;
    private final Money unitPrice;        // copia del precio al momento de la compra
    private int quantity;
    private Long warehouseId;             // bodega donde se reservó (null si es digital)

    public OrderItem(Long productId, String productName, ProductType productType,
                     Money unitPrice, int quantity, Long warehouseId) {
        Guard.requireNonNull(productId, "El producto");
        Guard.requireNonNull(productType, "El tipo de producto");
        Guard.requireNonNull(unitPrice, "El precio de unidad");
        Guard.requirePositive(quantity, "La cantidad");
        this.productId = productId;
        this.productName = Guard.requireText(productName, "El nombre del producto");
        this.productType = productType;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.warehouseId = warehouseId;
    }

    public static OrderItem from(Product product, int quantity) {
        return new OrderItem(product.getId(), product.getName(), product.getType(),
                product.getPrice(), quantity, null);
    }

    public void increase(int units) {
        Guard.requirePositive(units, "La cantidad");
        this.quantity += units;
    }

    public void assignWarehouse(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }

    public boolean isPhysical() { return productType == ProductType.PHYSICAL; }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public ProductType getProductType() { return productType; }
    public Money getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public Long getWarehouseId() { return warehouseId; }
}