package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.domain.models.Buyer;
import application.domain.valueobjects.CommercialStatus;
import java.util.ArrayList;

public final class BuyerJpaMapper {

    private BuyerJpaMapper() {
    }

    public static BuyerJpaEntity toEntity(Buyer domain) {
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setUserId(domain.getUserId());
        entity.setPrimaryAddress(domain.getPrimaryAddress());
        entity.setCommercialStatus(domain.getCommercialStatus().name());
        entity.setAdditionalAddresses(new ArrayList<>(domain.getAdditionalAddresses()));
        return entity;
    }

    public static Buyer toDomain(BuyerJpaEntity entity) {
        return new Buyer(entity.getUserId(), entity.getPrimaryAddress(),
                entity.getAdditionalAddresses(),
                CommercialStatus.valueOf(entity.getCommercialStatus()));
    }
}