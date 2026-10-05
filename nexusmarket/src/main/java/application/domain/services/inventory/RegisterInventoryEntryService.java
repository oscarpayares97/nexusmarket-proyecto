package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.ports.in.RegisterInventoryEntryUseCase;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.MovementType;

public class RegisterInventoryEntryService implements RegisterInventoryEntryUseCase {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryOperationGuard guard;

    public RegisterInventoryEntryService(InventoryRepositoryPort inventoryRepository,
                                         InventoryOperationGuard guard) {
        this.inventoryRepository = inventoryRepository;
        this.guard = guard;
    }

    @Override
    public Inventory register(User actor, Command command) {
        Inventory inventory = guard.load(actor, command.productId(), command.warehouseId(), true);
        inventory.registerEntry(command.quantity());
        Inventory saved = inventoryRepository.save(inventory);
        guard.recordMovement(MovementType.INFLOW, saved, command.quantity(), actor);
        return saved;
    }
}