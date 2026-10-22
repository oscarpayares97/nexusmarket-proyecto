package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.MovementType;

public final class InventoryJpaMapper {

    private InventoryJpaMapper() {
    }

    public static InventoryJpaEntity toEntity(Inventory domain) {
        InventoryJpaEntity entity = new InventoryJpaEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setAvailable(domain.getAvailable());
        entity.setReserved(domain.getReserved());
        entity.setStatus(domain.getStatus().name());
        return entity;
    }

    public static Inventory toDomain(InventoryJpaEntity entity) {
        return new Inventory(entity.getId(), entity.getProductId(), entity.getWarehouseId(),
                entity.getAvailable(), entity.getReserved(),
                InventoryStatus.valueOf(entity.getStatus()));
    }

    public static InventoryMovementJpaEntity toEntity(InventoryMovement domain) {
        InventoryMovementJpaEntity entity = new InventoryMovementJpaEntity();
        entity.setId(domain.getId());
        entity.setType(domain.getType().name());
        entity.setProductId(domain.getProductId());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setQuantity(domain.getQuantity());
        entity.setUserId(domain.getUserId());
        entity.setOccurredAt(domain.getOccurredAt());
        return entity;
    }

    public static InventoryMovement toDomain(InventoryMovementJpaEntity entity) {
        return new InventoryMovement(entity.getId(), MovementType.valueOf(entity.getType()),
                entity.getProductId(), entity.getWarehouseId(), entity.getQuantity(),
                entity.getUserId(), entity.getOccurredAt());
    }
}