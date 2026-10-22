package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.domain.models.Invoice;
import application.domain.valueobjects.Money;

public final class InvoiceJpaMapper {

    private InvoiceJpaMapper() {
    }

    public static InvoiceJpaEntity toEntity(Invoice domain) {
        InvoiceJpaEntity entity = new InvoiceJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrderId());
        entity.setBuyerId(domain.getBuyerId());
        entity.setBuyerName(domain.getBuyerName());
        entity.setBuyerDocument(domain.getBuyerDocument());
        entity.setTotal(domain.getTotal().amount());
        entity.setIssuedAt(domain.getIssuedAt());
        entity.setLines(OrderJpaMapper.toEmbeddables(domain.getLines()));
        return entity;
    }

    public static Invoice toDomain(InvoiceJpaEntity entity) {
        return new Invoice(entity.getId(), entity.getOrderId(), entity.getBuyerId(),
                entity.getBuyerName(), entity.getBuyerDocument(),
                OrderJpaMapper.toDomainItems(entity.getLines()),
                new Money(entity.getTotal()), entity.getIssuedAt());
    }
}