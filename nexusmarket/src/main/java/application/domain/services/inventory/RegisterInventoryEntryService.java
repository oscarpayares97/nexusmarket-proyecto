package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.MovementType;

public class RegisterInventoryEntryService {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryOperationGuard guard;

    public RegisterInventoryEntryService(InventoryRepositoryPort inventoryRepository,
                                         InventoryOperationGuard guard) {
        this.inventoryRepository = inventoryRepository;
        this.guard = guard;
    }

    public Inventory register(User actor, Long productId, Long warehouseId, int quantity) {
        Inventory inventory = guard.load(actor, productId, warehouseId, true);
        inventory.registerEntry(quantity);
        Inventory saved = inventoryRepository.save(inventory);
        guard.recordMovement(MovementType.INFLOW, saved, quantity, actor);
        return saved;
    }
}