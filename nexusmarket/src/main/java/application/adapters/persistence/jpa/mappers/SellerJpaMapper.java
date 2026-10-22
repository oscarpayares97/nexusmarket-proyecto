package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import application.domain.models.Seller;

public final class SellerJpaMapper {

    private SellerJpaMapper() {
    }

    public static SellerJpaEntity toEntity(Seller domain) {
        SellerJpaEntity entity = new SellerJpaEntity();
        entity.setUserId(domain.getUserId());
        entity.setTradeName(domain.getTradeName());
        return entity;
    }

    public static Seller toDomain(SellerJpaEntity entity) {
        return new Seller(entity.getUserId(), entity.getTradeName());
    }
}