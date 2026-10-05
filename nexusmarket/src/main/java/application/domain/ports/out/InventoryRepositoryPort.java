package application.domain.ports.out;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {

    Inventory save(Inventory item);

    Optional<Inventory> findByProductAndWarehouse(Long productId, Long warehouseId);

    List<Inventory> findByProductId(Long productId);

    InventoryMovement saveMovement(InventoryMovement movement);

    List<InventoryMovement> findAllMovements();
}