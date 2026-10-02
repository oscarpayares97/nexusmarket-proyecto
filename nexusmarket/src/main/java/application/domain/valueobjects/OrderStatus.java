package application.domain.valueobjects;

public enum OrderStatus {
    CART,
    PENDING_PAYMENT,
    PAID,
    DISPATCHED,
    COMPLETED     // "Entregado / Finalizado" en la especificación
}