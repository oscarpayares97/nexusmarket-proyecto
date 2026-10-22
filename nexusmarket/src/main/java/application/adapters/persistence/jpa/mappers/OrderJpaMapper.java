package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.OrderItemJpaEmbeddable;
import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductType;
import java.util.ArrayList;
import java.util.List;

public final class OrderJpaMapper {

    private OrderJpaMapper() {
    }

    public static OrderJpaEntity toEntity(Order domain) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(domain.getId());
        entity.setBuyerId(domain.getBuyerId());
        entity.setStatus(domain.getStatus().name());
        entity.setDeliveryAddress(domain.getDeliveryAddress());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setItems(toEmbeddables(domain.getItems()));
        return entity;
    }

    public static Order toDomain(OrderJpaEntity entity) {
        return new Order(entity.getId(), entity.getBuyerId(), toDomainItems(entity.getItems()),
                OrderStatus.valueOf(entity.getStatus()), entity.getDeliveryAddress(),
                entity.getCreatedAt());
    }

    // Se reutilizan desde InvoiceJpaMapper (la factura copia las líneas del pedido)
    public static List<OrderItemJpaEmbeddable> toEmbeddables(List<OrderItem> items) {
        List<OrderItemJpaEmbeddable> rows = new ArrayList<>();
        for (OrderItem item : items) {
            rows.add(toEmbeddable(item));
        }
        return rows;
    }

    public static List<OrderItem> toDomainItems(List<OrderItemJpaEmbeddable> rows) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemJpaEmbeddable row : rows) {
            items.add(toDomainItem(row));
        }
        return items;
    }

    private static OrderItemJpaEmbeddable toEmbeddable(OrderItem item) {
        OrderItemJpaEmbeddable row = new OrderItemJpaEmbeddable();
        row.setProductId(item.getProductId());
        row.setProductName(item.getProductName());
        row.setProductType(item.getProductType().name());
        row.setUnitPrice(item.getUnitPrice().amount());
        row.setQuantity(item.getQuantity());
        row.setWarehouseId(item.getWarehouseId());
        return row;
    }

    private static OrderItem toDomainItem(OrderItemJpaEmbeddable row) {
        return new OrderItem(row.getProductId(), row.getProductName(),
                ProductType.valueOf(row.getProductType()), new Money(row.getUnitPrice()),
                row.getQuantity(), row.getWarehouseId());
    }
}