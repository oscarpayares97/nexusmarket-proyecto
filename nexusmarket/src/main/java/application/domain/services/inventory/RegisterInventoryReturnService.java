package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.in.RegisterInventoryReturnUseCase;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.MovementType;

public class RegisterInventoryReturnService implements RegisterInventoryReturnUseCase {

    private final InventoryRepositoryPort inventoryRepository;

    public RegisterInventoryReturnService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void registerReturn(User operator, Long productId, Long warehouseId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found."));
        inventory.registerCustomerReturn(quantity);
        inventoryRepository.save(inventory);
        inventoryRepository.saveMovement(new InventoryMovement(null, MovementType.RETURN,
                productId, warehouseId, quantity, operator.getId()));
    }
}