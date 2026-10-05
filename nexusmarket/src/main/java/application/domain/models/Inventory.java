package application.domain.models;

import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidDataException;
import application.domain.valueobjects.InventoryStatus;

public class Inventory {

    private Long id;
    private final Long productId;
    private final Long warehouseId;
    private int available;     // unidades que se pueden vender
    private int reserved;      // unidades apartadas para pedidos
    private InventoryStatus status;

    public Inventory(Long id, Long productId, Long warehouseId) {
        this(id, productId, warehouseId, 0, 0, InventoryStatus.AVAILABLE);
    }

    public Inventory(Long id, Long productId, Long warehouseId,
                         int available, int reserved, InventoryStatus status) {
        Guard.requireNonNull(productId, "El producto");
        Guard.requireNonNull(warehouseId, "El almacen");
        Guard.requireNonNull(status, "El estado del inventario");
        if (available < 0 || reserved < 0) {
            throw new InvalidDataException("El stock no puede ser negativo.");
        }
        this.id = id;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.available = available;
        this.reserved = reserved;
        this.status = status;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("El artículo de inventario ya tiene un identificador.");
        }
        this.id = id;
    }

    public void registerEntry(int units) {
        Guard.requirePositive(units, "La cantidad");
        available += units;
    }

    public void reserve(int units) {
        Guard.requirePositive(units, "La cantidad");
        if (status == InventoryStatus.DAMAGED) {
            throw new InvalidDataException("No se puede reservar el stock dañado.");
        }
        if (units > available) {
            throw new InsufficientStockException(
                "Stock insuficiente. Disponible: " + available + ", respuesta: " + units + ".");
        }
        available -= units;
        reserved += units;
    }

    // La venta consume unidades que ya estaban reservadas
    public void registerSaleExit(int units) {
        Guard.requirePositive(units, "La cantidad");
        if (units > reserved) {
            throw new InsufficientStockException(
                "Existencias reservadas insuficientes. Reservado: " + reserved + ", Respuesta: " + units + ".");
        }
        reserved -= units;
    }

    // Ajuste: fija lo disponible a la cantidad contada físicamente
    public void adjustTo(int newAvailable) {
        if (newAvailable < 0) {
            throw new InvalidDataException("La cantidad no puede ser negativa.");
        }
        available = newAvailable;
    }

    public void registerCustomerReturn(int units) {
        Guard.requirePositive(units, "La cantidad");
        available += units;
    }

    public void markDamaged() { this.status = InventoryStatus.DAMAGED; }
    public void markAvailable() { this.status = InventoryStatus.AVAILABLE; }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getWarehouseId() { return warehouseId; }
    public int getAvailable() { return available; }
    public int getReserved() { return reserved; }
    public InventoryStatus getStatus() { return status; }
}