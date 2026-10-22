package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseType;

public final class WarehouseJpaMapper {

    private WarehouseJpaMapper() {
    }

    public static WarehouseJpaEntity toEntity(Warehouse domain) {
        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setLocation(domain.getLocation());
        entity.setType(domain.getType().name());
        entity.setSellerId(domain.getSellerId());
        entity.setActive(domain.isActive());
        return entity;
    }

    public static Warehouse toDomain(WarehouseJpaEntity entity) {
        return new Warehouse(entity.getId(), entity.getName(), entity.getLocation(),
                WarehouseType.valueOf(entity.getType()), entity.getSellerId(), entity.isActive());
    }
}