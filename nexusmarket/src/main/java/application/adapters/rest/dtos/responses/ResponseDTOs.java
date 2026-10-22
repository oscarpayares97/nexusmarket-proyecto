package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Respuestas JSON de la API. Solo datos que el cliente debe ver (nunca el passwordHash). */
public final class ResponseDTOs {

    private ResponseDTOs() {
    }

    public record UserResponse(Long id, String document, String fullName, String email,
                               String role, String status) {
    }

    public record BuyerProfileResponse(Long userId, String primaryAddress,
                                       List<String> additionalAddresses, String commercialStatus) {
    }

    public record SellerResponse(Long userId, String tradeName) {
    }

    public record WarehouseResponse(Long id, String name, String location, String type,
                                    Long sellerId) {
    }

    public record ProductResponse(Long id, Long sellerId, String name, String type,
                                  BigDecimal price, String status, List<String> variants) {
    }

    public record OrderItemResponse(Long productId, String productName, String productType,
                                    BigDecimal unitPrice, int quantity, BigDecimal subtotal,
                                    Long warehouseId) {
    }

    public record OrderResponse(Long id, Long buyerId, String status, String deliveryAddress,
                                LocalDateTime createdAt, List<OrderItemResponse> items,
                                BigDecimal total) {
    }

    public record InvoiceResponse(Long id, String number, Long orderId, Long buyerId,
                                  String buyerName, String buyerDocument,
                                  List<OrderItemResponse> lines, BigDecimal total,
                                  LocalDateTime issuedAt) {
    }

    public record ShipmentResponse(Long id, Long orderId, String deliveryAddress, String status,
                                   String carrier, String trackingNumber, LocalDateTime createdAt,
                                   LocalDateTime shippedAt, LocalDateTime deliveredAt) {
    }

    public record ReturnItemResponse(Long productId, String productName, int quantity,
                                     BigDecimal unitPrice, BigDecimal subtotal) {
    }

    public record ReturnResponse(Long id, Long orderId, Long buyerId,
                                 List<ReturnItemResponse> items, String reason, String status,
                                 String rejectionReason, LocalDateTime requestedAt,
                                 BigDecimal total) {
    }

    public record RefundResponse(Long id, Long returnRequestId, Long orderId, Long buyerId,
                                 BigDecimal amount, String status, LocalDateTime createdAt,
                                 LocalDateTime processedAt) {
    }

    public record InventoryResponse(Long id, Long productId, Long warehouseId, int available,
                                    int reserved, String status) {
    }

    public record MovementResponse(Long id, String type, Long productId, Long warehouseId,
                                   int quantity, Long userId, LocalDateTime occurredAt) {
    }

    public record SalesReportResponse(BigDecimal grossSales, BigDecimal refunded,
                                      BigDecimal netSales, Map<String, Integer> ordersByStatus,
                                      Map<Long, BigDecimal> salesBySeller) {
    }
}