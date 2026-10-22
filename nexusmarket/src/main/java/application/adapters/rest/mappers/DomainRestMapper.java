package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.ResponseDTOs.BuyerProfileResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.InventoryResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.InvoiceResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.MovementResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.OrderItemResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.OrderResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ProductResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.RefundResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ReturnItemResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ReturnResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.SalesReportResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.SellerResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ShipmentResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.UserResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.WarehouseResponse;
import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.models.SalesReport;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Convierte objetos del dominio en DTOs de respuesta. Métodos estáticos, sin lógica de negocio. */
public final class DomainRestMapper {

    private DomainRestMapper() {
    }

    public static <T, R> List<R> mapList(List<T> source, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : source) {
            result.add(mapper.apply(item));
        }
        return result;
    }

    private static BigDecimal amount(Money money) {
        return money == null ? null : money.amount();
    }

    public static UserResponse toUser(User user) {
        return new UserResponse(user.getId(), user.getDocument().value(), user.getFullName(),
                user.getEmail().value(), user.getRole().name(), user.getStatus().name());
    }

    public static BuyerProfileResponse toBuyer(Buyer buyer) {
        return new BuyerProfileResponse(buyer.getUserId(), buyer.getPrimaryAddress(),
                buyer.getAdditionalAddresses(), buyer.getCommercialStatus().name());
    }

    public static SellerResponse toSeller(Seller seller) {
        return new SellerResponse(seller.getUserId(), seller.getTradeName());
    }

    public static WarehouseResponse toWarehouse(Warehouse warehouse) {
        return new WarehouseResponse(warehouse.getId(), warehouse.getName(), warehouse.getLocation(),
                warehouse.getType().name(), warehouse.getSellerId());
    }

    public static ProductResponse toProduct(Product product) {
        return new ProductResponse(product.getId(), product.getSellerId(), product.getName(),
                product.getType().name(), amount(product.getPrice()), product.getStatus().name(),
                product.getVariants());
    }

    public static OrderItemResponse toOrderItem(OrderItem item) {
        return new OrderItemResponse(item.getProductId(), item.getProductName(),
                item.getProductType().name(), amount(item.getUnitPrice()), item.getQuantity(),
                amount(item.subtotal()), item.getWarehouseId());
    }

    public static OrderResponse toOrder(Order order) {
        return new OrderResponse(order.getId(), order.getBuyerId(), order.getStatus().name(),
                order.getDeliveryAddress(), order.getCreatedAt(),
                mapList(order.getItems(), DomainRestMapper::toOrderItem), amount(order.total()));
    }

    public static InvoiceResponse toInvoice(Invoice invoice) {
        return new InvoiceResponse(invoice.getId(), invoice.getNumber(), invoice.getOrderId(),
                invoice.getBuyerId(), invoice.getBuyerName(), invoice.getBuyerDocument(),
                mapList(invoice.getLines(), DomainRestMapper::toOrderItem),
                amount(invoice.getTotal()), invoice.getIssuedAt());
    }

    public static ShipmentResponse toShipment(Shipment shipment) {
        return new ShipmentResponse(shipment.getId(), shipment.getOrderId(),
                shipment.getDeliveryAddress(), shipment.getStatus().name(), shipment.getCarrier(),
                shipment.getTrackingNumber(), shipment.getCreatedAt(), shipment.getShippedAt(),
                shipment.getDeliveredAt());
    }

    public static ReturnItemResponse toReturnItem(ReturnItem item) {
        return new ReturnItemResponse(item.getProductId(), item.getProductName(), item.getQuantity(),
                amount(item.getUnitPrice()), amount(item.subtotal()));
    }

    public static ReturnResponse toReturn(ReturnRequest request) {
        return new ReturnResponse(request.getId(), request.getOrderId(), request.getBuyerId(),
                mapList(request.getItems(), DomainRestMapper::toReturnItem), request.getReason(),
                request.getStatus().name(), request.getRejectionReason(), request.getRequestedAt(),
                amount(request.total()));
    }

    public static RefundResponse toRefund(Refund refund) {
        return new RefundResponse(refund.getId(), refund.getReturnRequestId(), refund.getOrderId(),
                refund.getBuyerId(), amount(refund.getAmount()), refund.getStatus().name(),
                refund.getCreatedAt(), refund.getProcessedAt());
    }

    public static InventoryResponse toInventory(Inventory inventory) {
        return new InventoryResponse(inventory.getId(), inventory.getProductId(),
                inventory.getWarehouseId(), inventory.getAvailable(), inventory.getReserved(),
                inventory.getStatus().name());
    }

    public static MovementResponse toMovement(InventoryMovement movement) {
        return new MovementResponse(movement.getId(), movement.getType().name(),
                movement.getProductId(), movement.getWarehouseId(), movement.getQuantity(),
                movement.getUserId(), movement.getOccurredAt());
    }

    public static SalesReportResponse toSalesReport(SalesReport report) {
        Map<String, Integer> byStatus = new LinkedHashMap<>();
        for (Map.Entry<OrderStatus, Integer> entry : report.ordersByStatus().entrySet()) {
            byStatus.put(entry.getKey().name(), entry.getValue());
        }
        Map<Long, BigDecimal> bySeller = new LinkedHashMap<>();
        for (Map.Entry<Long, Money> entry : report.salesBySeller().entrySet()) {
            bySeller.put(entry.getKey(), amount(entry.getValue()));
        }
        return new SalesReportResponse(amount(report.grossSales()), amount(report.refunded()),
                amount(report.netSales()), byStatus, bySeller);
    }
}