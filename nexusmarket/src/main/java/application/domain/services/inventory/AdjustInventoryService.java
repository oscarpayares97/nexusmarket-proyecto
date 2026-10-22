package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.MovementType;

public class AdjustInventoryService {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryOperationGuard guard;

    public AdjustInventoryService(InventoryRepositoryPort inventoryRepository,
                                  InventoryOperationGuard guard) {
        this.inventoryRepository = inventoryRepository;
        this.guard = guard;
    }

    public Inventory adjust(User actor, Long productId, Long warehouseId, int newQuantity) {
        Inventory inventory = guard.load(actor, productId, warehouseId, false);
        inventory.adjustTo(newQuantity);
        Inventory saved = inventoryRepository.save(inventory);
        guard.recordMovement(MovementType.ADJUSTMENT, saved, newQuantity, actor);
        return saved;
    }
}