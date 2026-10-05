package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.User;

public interface RegisterInventoryEntryUseCase {

    Inventory register(User actor, Command command);

    record Command(Long productId, Long warehouseId, int quantity) {
    }
}