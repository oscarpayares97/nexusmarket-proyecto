package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ReturnStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReturnRequest {

    private Long id;
    private final Long orderId;
    private final Long buyerId;
    private final List<ReturnItem> items = new ArrayList<>();
    private final String reason;
    private ReturnStatus status;
    private String rejectionReason;
    private final LocalDateTime requestedAt;

    public ReturnRequest(Long id, Long orderId, Long buyerId, List<ReturnItem> items, String reason) {
        this(id, orderId, buyerId, items, reason, ReturnStatus.REQUESTED, null, LocalDateTime.now());
    }

    public ReturnRequest(Long id, Long orderId, Long buyerId, List<ReturnItem> items, String reason,
                         ReturnStatus status, String rejectionReason, LocalDateTime requestedAt) {
        Guard.requireNonNull(orderId, "El pedido");
        Guard.requireNonNull(buyerId, "El vendedor");
        Guard.requireNonNull(status, "El estado");
        Guard.requireNonNull(requestedAt, "Fecha de solicitud");
        if (items == null || items.isEmpty()) {
            throw new InvalidDataException("Una devolución debe incluir al menos un producto.");
        }
        this.id = id;
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.items.addAll(items);
        this.reason = Guard.requireText(reason, "La razon");
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.requestedAt = requestedAt;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("La devolución ya tiene un número de referencia.");
        }
        this.id = id;
    }

    public Money total() {
        Money total = Money.ZERO;
        for (ReturnItem item : items) {
            total = total.add(item.subtotal());
        }
        return total;
    }

    public void approve() {
        requireStatus(ReturnStatus.REQUESTED, "Aprovado");
        this.status = ReturnStatus.APPROVED;
    }

    public void reject(String rejectionReason) {
        requireStatus(ReturnStatus.REQUESTED, "Rechazado");
        this.rejectionReason = Guard.requireText(rejectionReason, "The rejection reason");
        this.status = ReturnStatus.REJECTED;
    }

    public void markReceived() {
        requireStatus(ReturnStatus.APPROVED, "Recibido");
        this.status = ReturnStatus.RECEIVED;
    }

    private void requireStatus(ReturnStatus expected, String action) {
        if (status != expected) {
            throw new InvalidStatusTransitionException("No se puede " + action + ": El resultado es "
                    + status + " Y debe ser " + expected + ".");
        }
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getBuyerId() { return buyerId; }
    public String getReason() { return reason; }
    public ReturnStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public List<ReturnItem> getItems() { return new ArrayList<>(items); }
}