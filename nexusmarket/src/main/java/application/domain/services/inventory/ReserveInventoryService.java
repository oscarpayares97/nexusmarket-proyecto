package application.domain.services.inventory;

import application.domain.exceptions.InsufficientStockException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.MovementType;

import java.util.Optional;

public class ReserveInventoryService {

    private final InventoryRepositoryPort inventoryRepository;

    public ReserveInventoryService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public boolean hasStock(Long productId, int quantity) {
        return findWithStock(productId, quantity).isPresent();
    }

    public Long reserve(User buyer, Long productId, int quantity) {
        Inventory inventory = findWithStock(productId, quantity)
                .orElseThrow(() -> new InsufficientStockException(
                        "Insufficient stock for product " + productId + "."));
        inventory.reserve(quantity);
        inventoryRepository.save(inventory);
        inventoryRepository.saveMovement(new InventoryMovement(null, MovementType.RESERVATION,
                productId, inventory.getWarehouseId(), quantity, buyer.getId()));
        return inventory.getWarehouseId();
    }

    private Optional<Inventory> findWithStock(Long productId, int quantity) {
        for (Inventory inventory : inventoryRepository.findByProductId(productId)) {
            if (inventory.getStatus() == InventoryStatus.AVAILABLE && inventory.getAvailable() >= quantity) {
                return Optional.of(inventory);
            }
        }
        return Optional.empty();
    }
}