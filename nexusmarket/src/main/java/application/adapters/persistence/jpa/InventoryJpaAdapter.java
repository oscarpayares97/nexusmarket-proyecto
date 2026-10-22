package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.adapters.persistence.jpa.mappers.InventoryJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInventoryMovementRepository;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInventoryRepository;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.ports.out.InventoryRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryJpaAdapter implements InventoryRepositoryPort {

    private final SpringDataJpaInventoryRepository inventoryRepository;
    private final SpringDataJpaInventoryMovementRepository movementRepository;

    public InventoryJpaAdapter(SpringDataJpaInventoryRepository inventoryRepository,
                               SpringDataJpaInventoryMovementRepository movementRepository) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    public Inventory save(Inventory inventory) {
        InventoryJpaEntity saved = inventoryRepository.save(InventoryJpaMapper.toEntity(inventory));
        if (inventory.getId() == null) {
            inventory.assignId(saved.getId());
        }
        return inventory;
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(Long productId, Long warehouseId) {
        return inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .map(InventoryJpaMapper::toDomain);
    }

    @Override
    public List<Inventory> findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId).stream()
                .map(InventoryJpaMapper::toDomain).toList();
    }

    @Override
    public InventoryMovement saveMovement(InventoryMovement movement) {
        InventoryMovementJpaEntity saved = movementRepository.save(InventoryJpaMapper.toEntity(movement));
        if (movement.getId() == null) {
            movement.assignId(saved.getId());
        }
        return movement;
    }

    @Override
    public List<InventoryMovement> findAllMovements() {
        return movementRepository.findAll().stream().map(InventoryJpaMapper::toDomain).toList();
    }
}