package application.domain.ports.out;

import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {

    InventoryItem save(InventoryItem item);

    Optional<InventoryItem> findByProductAndWarehouse(Long productId, Long warehouseId);

    List<InventoryItem> findByProductId(Long productId);

    InventoryMovement saveMovement(InventoryMovement movement);

    List<InventoryMovement> findAllMovements();
}