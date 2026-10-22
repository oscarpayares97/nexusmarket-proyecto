package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import application.adapters.persistence.jpa.entities.ReturnItemJpaEmbeddable;
import application.adapters.persistence.jpa.entities.ReturnRequestJpaEntity;
import application.domain.models.Refund;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import java.util.ArrayList;
import java.util.List;


public final class ReturnJpaMapper {

    private ReturnJpaMapper() {
    }

    public static ReturnRequestJpaEntity toEntity(ReturnRequest domain) {
        ReturnRequestJpaEntity entity = new ReturnRequestJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrderId());
        entity.setBuyerId(domain.getBuyerId());
        entity.setReason(domain.getReason());
        entity.setStatus(domain.getStatus().name());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setRequestedAt(domain.getRequestedAt());
        List<ReturnItemJpaEmbeddable> rows = new ArrayList<>();
        for (ReturnItem item : domain.getItems()) {
            rows.add(toEmbeddable(item));
        }
        entity.setItems(rows);
        return entity;
    }

    public static ReturnRequest toDomain(ReturnRequestJpaEntity entity) {
                List<ReturnItem> items = new ArrayList<>();
        for (ReturnItemJpaEmbeddable row : entity.getItems()) {
            items.add(toDomainItem(row));
        }
        return new ReturnRequest(entity.getId(), entity.getOrderId(), entity.getBuyerId(), items,
                entity.getReason(), ReturnStatus.valueOf(entity.getStatus()),
                entity.getRejectionReason(), entity.getRequestedAt());
    }

    public static RefundJpaEntity toEntity(Refund domain) {
        RefundJpaEntity entity = new RefundJpaEntity();
        entity.setId(domain.getId());
        entity.setReturnRequestId(domain.getReturnRequestId());
        entity.setOrderId(domain.getOrderId());
        entity.setBuyerId(domain.getBuyerId());
        entity.setAmount(domain.getAmount().amount());
        entity.setStatus(domain.getStatus().name());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setProcessedAt(domain.getProcessedAt());
        return entity;
    }

    public static Refund toDomain(RefundJpaEntity entity) {
        return new Refund(entity.getId(), entity.getReturnRequestId(), entity.getOrderId(),
                entity.getBuyerId(), new Money(entity.getAmount()),
                RefundStatus.valueOf(entity.getStatus()), entity.getCreatedAt(),
                entity.getProcessedAt());
    }

    private static ReturnItemJpaEmbeddable toEmbeddable(ReturnItem item) {
        ReturnItemJpaEmbeddable row = new ReturnItemJpaEmbeddable();
        row.setProductId(item.getProductId());
        row.setProductName(item.getProductName());
        row.setQuantity(item.getQuantity());
        row.setUnitPrice(item.getUnitPrice().amount());
        row.setWarehouseId(item.getWarehouseId());
        return row;
    }

    private static ReturnItem toDomainItem(ReturnItemJpaEmbeddable row) {
        return new ReturnItem(row.getProductId(), row.getProductName(), row.getQuantity(),
                new Money(row.getUnitPrice()), row.getWarehouseId());
    }
}