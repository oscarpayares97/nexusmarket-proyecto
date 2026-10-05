package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.ShipmentStatus;

import java.time.LocalDateTime;

public class Shipment {

    private Long id;
    private final Long orderId;
    private final String deliveryAddress;
    private ShipmentStatus status;
    private String carrier;
    private String trackingNumber;
    private final LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    public Shipment(Long id, Long orderId, String deliveryAddress) {
        this(id, orderId, deliveryAddress, ShipmentStatus.PREPARING, null, null,
                LocalDateTime.now(), null, null);
    }

    public Shipment(Long id, Long orderId, String deliveryAddress, ShipmentStatus status,
                    String carrier, String trackingNumber, LocalDateTime createdAt,
                    LocalDateTime shippedAt, LocalDateTime deliveredAt) {
        Guard.requireNonNull(orderId, "La orden");
        Guard.requireNonNull(status, "El estado");
        Guard.requireNonNull(createdAt, "Fecha de creacion");
        this.id = id;
        this.orderId = orderId;
        this.deliveryAddress = Guard.requireText(deliveryAddress, "La dirección de entrega");
        this.status = status;
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.createdAt = createdAt;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("El envío ya tiene un número de identificación.");
        }
        this.id = id;
    }

    public void pack() {
        requireStatus(ShipmentStatus.PREPARING, "Paquete");
        this.status = ShipmentStatus.PACKED;
    }

    public void dispatch(String carrier, String trackingNumber) {
        requireStatus(ShipmentStatus.PACKED, "Despacho");
        this.carrier = Guard.requireText(carrier, "Compañia aerea");
        this.trackingNumber = Guard.requireText(trackingNumber, "El número de seguimiento");
        this.shippedAt = LocalDateTime.now();
        this.status = ShipmentStatus.SHIPPED;
    }

    public void markInTransit() {
        requireStatus(ShipmentStatus.SHIPPED, "Registrar el transito");
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    public void confirmDelivery() {
        requireStatus(ShipmentStatus.IN_TRANSIT, "Confirmacion de la entrega");
        this.deliveredAt = LocalDateTime.now();
        this.status = ShipmentStatus.DELIVERED;
    }

    private void requireStatus(ShipmentStatus expected, String action) {
        if (status != expected) {
            throw new InvalidStatusTransitionException("No se puede " + action + ": El envio tiene el estado "
                    + status + " y debe ser " + expected + ".");
        }
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public ShipmentStatus getStatus() { return status; }
    public String getCarrier() { return carrier; }
    public String getTrackingNumber() { return trackingNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getShippedAt() { return shippedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
}