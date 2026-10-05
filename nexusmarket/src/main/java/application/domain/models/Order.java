package application.domain.models;

import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {

    private Long id;
    private final Long buyerId;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private String deliveryAddress;
    private final LocalDateTime createdAt;

    public Order(Long id, Long buyerId) {
        this(id, buyerId, new ArrayList<>(), OrderStatus.CART, null, LocalDateTime.now());
    }

    public Order(Long id, Long buyerId, List<OrderItem> items, OrderStatus status,
                 String deliveryAddress, LocalDateTime createdAt) {
        Guard.requireNonNull(buyerId, "El vendedor");
        Guard.requireNonNull(status, "El estado");
        Guard.requireNonNull(createdAt, "La fecha de creación");
        this.id = id;
        this.buyerId = buyerId;
        if (items != null) {
            this.items.addAll(items);
        }
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.createdAt = createdAt;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new InvalidDataException("El pedido ya tiene un número de identificación.");
        }
        this.id = id;
    }

    // ---------- Carrito ----------
    public void addItem(Product product, int quantity) {
        requireStatus(OrderStatus.CART, "Modifica el producto");
        Guard.requirePositive(quantity, "La cantidad");
        for (OrderItem item : items) {
            // Los Long se comparan con equals (no con ==)
            if (Objects.equals(item.getProductId(), product.getId())) {
                item.increase(quantity);
                return;
            }
        }
        items.add(OrderItem.from(product, quantity));
    }

    public void removeItem(Long productId) {
        requireStatus(OrderStatus.CART, "Modifica el producto");
        for (int i = 0; i < items.size(); i++) {
            if (Objects.equals(items.get(i).getProductId(), productId)) {
                items.remove(i);
                return;
            }
        }
        throw new InvalidDataException("Ese producto no esta en la canasta.");
    }

    public Money total() {
        Money total = Money.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.subtotal());
        }
        return total;
    }

    // true si el pedido no tiene productos físicos
    public boolean onlyDigital() {
        for (OrderItem item : items) {
            if (item.isPhysical()) {
                return false;
            }
        }
        return !items.isEmpty();
    }

    // ---------- Ciclo de vida ----------
    public void confirm(String deliveryAddress) {
        requireStatus(OrderStatus.CART, "Confirmar la orden");
        if (items.isEmpty()) {
            throw new InvalidDataException("El carrito esta vacio.");
        }
        this.deliveryAddress = Guard.requireText(deliveryAddress, "La dirección de entrega");
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public void markPaid() {
        requireStatus(OrderStatus.PENDING_PAYMENT, "Pago");
        this.status = OrderStatus.PAID;
    }

    public void markShipped() {
        requireStatus(OrderStatus.PAID, "Despacho");
        this.status = OrderStatus.SHIPPED;
    }

    public void markDelivered() {
        requireStatus(OrderStatus.SHIPPED, "realizar la entrega");
        this.status = OrderStatus.DELIVERED;
    }

    // Regla central: ¿se puede hacer esta acción en el estado actual?
    private void requireStatus(OrderStatus expected, String action) {
        if (status == OrderStatus.DELIVERED) {
            throw new InvalidStatusTransitionException(
                "Un pedido entregado no se puede modificar bajo ninguna circunstancia.");
        }
        if (status != expected) {
            throw new InvalidStatusTransitionException("No se puede " + action + ": El orden es "
                    + status + " y debe ser " + expected + ".");
        }
    }

    public Long getId() { return id; }
    public Long getBuyerId() { return buyerId; }
    public OrderStatus getStatus() { return status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }
}