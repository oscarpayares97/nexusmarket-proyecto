package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.domain.models.Shipment;
import application.domain.valueobjects.ShipmentStatus;

public final class ShipmentJpaMapper {

    private ShipmentJpaMapper() {
    }

    public static ShipmentJpaEntity toEntity(Shipment domain) {
        ShipmentJpaEntity entity = new ShipmentJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrderId());
        entity.setDeliveryAddress(domain.getDeliveryAddress());
        entity.setStatus(domain.getStatus().name());
        entity.setCarrier(domain.getCarrier());
        entity.setTrackingNumber(domain.getTrackingNumber());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setShippedAt(domain.getShippedAt());
        entity.setDeliveredAt(domain.getDeliveredAt());
        return entity;
    }

    public static Shipment toDomain(ShipmentJpaEntity entity) {
        return new Shipment(entity.getId(), entity.getOrderId(), entity.getDeliveryAddress(),
                ShipmentStatus.valueOf(entity.getStatus()), entity.getCarrier(),
                entity.getTrackingNumber(), entity.getCreatedAt(), entity.getShippedAt(),
                entity.getDeliveredAt());
    }
}