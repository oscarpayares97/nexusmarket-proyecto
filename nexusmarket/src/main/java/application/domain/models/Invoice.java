package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private Long id;
    private final Long orderId;
    private final Long buyerId;
    private final String buyerName;
    private final String buyerDocument;
    private final List<OrderItem> lines = new ArrayList<>();
    private final Money total;
    private final LocalDateTime issuedAt;

    public Invoice(Long id, Long orderId, Long buyerId, String buyerName, String buyerDocument,
                   List<OrderItem> lines, Money total, LocalDateTime issuedAt) {
        Guard.requireNonNull(orderId, "El pedido");
        Guard.requireNonNull(buyerId, "El vendedor");
        Guard.requireNonNull(lines, "Las lineas");
        Guard.requireNonNull(total, "El total");
        Guard.requireNonNull(issuedAt, "La fecha de emisión");
        if (lines.isEmpty()) {
            throw new InvalidDataException("Una factura debe contener al menos una línea.");
        }
        this.id = id;
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.buyerName = Guard.requireText(buyerName, "Nombre vendedor");
        this.buyerDocument = Guard.requireText(buyerDocument, "Documento del vendedor");
        this.lines.addAll(lines);
        this.total = total;
        this.issuedAt = issuedAt;
    }

    // Emite la factura de un pedido ya pagado
    public static Invoice issueFor(Order order, User buyer) {
        Guard.requireNonNull(order, "El pedido");
        Guard.requireNonNull(buyer, "El vendedor");
        if (order.getStatus() == OrderStatus.CART || order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            throw new InvalidStatusTransitionException("Solo se puede emitir una factura para un pedido pagado.");
        }
        return new Invoice(null, order.getId(), buyer.getId(), buyer.getFullName(),
                buyer.getDocument(), order.getItems(), order.total(), LocalDateTime.now());
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("La factura ya tiene un número de identificación.");
        }
        this.id = id;
    }

    public String getNumber() { return id == null ? null : String.format("INV-%06d", id); }
    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getBuyerId() { return buyerId; }
    public String getBuyerName() { return buyerName; }
    public String getBuyerDocument() { return buyerDocument; }
    public Money getTotal() { return total; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public List<OrderItem> getLines() { return new ArrayList<>(lines); }
}