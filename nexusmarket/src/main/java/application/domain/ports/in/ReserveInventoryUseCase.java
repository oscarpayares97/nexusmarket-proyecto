package application.domain.ports.in;

import application.domain.models.User;

public interface ReserveInventoryUseCase {

    // ¿Hay alguna bodega con inventario disponible suficiente?
    boolean hasStock(Long productId, int quantity);

    // Reserva en la primera bodega con stock y devuelve el id de esa bodega
    Long reserve(User buyer, Long productId, int quantity);
}