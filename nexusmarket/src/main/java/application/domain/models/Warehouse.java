package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.valueobjects.WarehouseType;

public class Warehouse {

    private Long id;
    private final String name;
    private final String location;
    private final WarehouseType type;
    private final Long sellerId;      // null si es una bodega del Marketplace
    private boolean active;

    public Warehouse(Long id, String name, String location, WarehouseType type, Long sellerId) {
        this(id, name, location, type, sellerId, true);
    }

    public Warehouse(Long id, String name, String location, WarehouseType type,
                     Long sellerId, boolean active) {
        this.id = id;
        this.name = Guard.requireText(name, "Nombre del almacen");
        this.location = Guard.requireText(location, "Ubicacion del almacen");
        Guard.requireNonNull(type, "Tipo de almacen");
        if (type == WarehouseType.SELLER && sellerId == null) {
            throw new InvalidDataException("Un almacén de vendedor debe tener un vendedor asociado.");
        }
        if (type == WarehouseType.MARKETPLACE && sellerId != null) {
            throw new InvalidDataException("Un almacén de una plataforma de comercio electrónico no pertenece a un vendedor.");
        }
        this.type = type;
        this.sellerId = sellerId;
        this.active = active;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("El almacén ya tiene un identificador.");
        }
        this.id = id;
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public WarehouseType getType() { return type; }
    public Long getSellerId() { return sellerId; }
    public boolean isActive() { return active; }
}