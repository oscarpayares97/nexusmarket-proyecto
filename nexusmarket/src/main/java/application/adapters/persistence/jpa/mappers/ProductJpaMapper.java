package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.domain.models.Product;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import java.util.ArrayList;

public final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    public static ProductJpaEntity toEntity(Product domain) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(domain.getId());
        entity.setSellerId(domain.getSellerId());
        entity.setName(domain.getName());
        entity.setType(domain.getType().name());
        entity.setPrice(domain.getPrice().amount());
        entity.setStatus(domain.getStatus().name());
        entity.setVariants(new ArrayList<>(domain.getVariants()));
        return entity;
    }

    public static Product toDomain(ProductJpaEntity entity) {
        return new Product(entity.getId(), entity.getSellerId(), entity.getName(),
                ProductType.valueOf(entity.getType()), new Money(entity.getPrice()),
                entity.getVariants(), ProductStatus.valueOf(entity.getStatus()));
    }
}