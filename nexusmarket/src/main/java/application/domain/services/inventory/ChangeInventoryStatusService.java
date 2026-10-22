package application.domain.services.inventory;

import application.domain.models.Guard;
import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.InventoryStatus;

public class ChangeInventoryStatusService {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryOperationGuard guard;

    public ChangeInventoryStatusService(InventoryRepositoryPort inventoryRepository,
                                        InventoryOperationGuard guard) {
        this.inventoryRepository = inventoryRepository;
        this.guard = guard;
    }

    public Inventory change(User actor, Long productId, Long warehouseId, InventoryStatus status) {
        Guard.requireNonNull(status, "Estado del inventario");
        Inventory inventory = guard.load(actor, productId, warehouseId, false);
        if (status == InventoryStatus.DAMAGED) {
            inventory.markDamaged();
        } else {
            inventory.markAvailable();
        }
        return inventoryRepository.save(inventory);
    }
}