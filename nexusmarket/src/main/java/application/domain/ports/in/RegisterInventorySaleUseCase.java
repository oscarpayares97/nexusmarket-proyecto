package application.domain.ports.in;

import application.domain.models.User;

public interface RegisterInventorySaleUseCase {

    void registerSale(User operator, Long productId, Long warehouseId, int quantity);
}