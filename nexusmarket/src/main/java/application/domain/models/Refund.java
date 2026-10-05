package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;

import java.time.LocalDateTime;

public class Refund {

    private Long id;
    private final Long returnRequestId;
    private final Long orderId;
    private final Long buyerId;
    private final Money amount;
    private RefundStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public Refund(Long id, Long returnRequestId, Long orderId, Long buyerId, Money amount) {
        this(id, returnRequestId, orderId, buyerId, amount, RefundStatus.PENDING,
                LocalDateTime.now(), null);
    }

    public Refund(Long id, Long returnRequestId, Long orderId, Long buyerId, Money amount,
                  RefundStatus status, LocalDateTime createdAt, LocalDateTime processedAt) {
        Guard.requireNonNull(returnRequestId, "El regreso");
        Guard.requireNonNull(orderId, "La orden");
        Guard.requireNonNull(buyerId, "El vendedor");
        Guard.requireNonNull(amount, "La cantidad");
        Guard.requireNonNull(status, "El estado");
        Guard.requireNonNull(createdAt, "Fecha de creacion");
        if (!amount.isGreaterThanZero()) {
            throw new InvalidDataException("El importe del reembolso debe ser superior a cero.");
        }
        this.id = id;
        this.returnRequestId = returnRequestId;
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    // El reembolso solo se crea para una devolución ya recibida en bodega
    public static Refund forReturn(ReturnRequest returnRequest) {
        if (returnRequest.getStatus() != ReturnStatus.RECEIVED) {
            throw new InvalidStatusTransitionException("Solo se puede crear un reembolso por una devolución recibida.");
        }
        return new Refund(null, returnRequest.getId(), returnRequest.getOrderId(),
                returnRequest.getBuyerId(), returnRequest.total());
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("El reembolso ya tiene un número de referencia.");
        }
        this.id = id;
    }

    public void process() {
        if (status != RefundStatus.PENDING) {
            throw new InvalidStatusTransitionException("El reembolso ya se ha tramitado.");
        }
        this.status = RefundStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getReturnRequestId() { return returnRequestId; }
    public Long getOrderId() { return orderId; }
    public Long getBuyerId() { return buyerId; }
    public Money getAmount() { return amount; }
    public RefundStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}