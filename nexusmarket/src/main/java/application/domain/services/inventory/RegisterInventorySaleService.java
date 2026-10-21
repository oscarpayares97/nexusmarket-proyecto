package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.MovementType;

public class RegisterInventorySaleService {

    private final InventoryRepositoryPort inventoryRepository;

    public RegisterInventorySaleService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void registerSale(User operator, Long productId, Long warehouseId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found."));
        inventory.registerSaleExit(quantity);
        inventoryRepository.save(inventory);
        inventoryRepository.saveMovement(new InventoryMovement(null, MovementType.SALE,
                productId, warehouseId, quantity, operator.getId()));
    }
}