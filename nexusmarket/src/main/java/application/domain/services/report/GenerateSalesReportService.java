package application.domain.services.report;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.SalesReport;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.UserRole;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class GenerateSalesReportService {

    private final InvoiceRepositoryPort invoiceRepository;
    private final RefundRepositoryPort refundRepository;
    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final ValidateRoleAuthorizationService authorization;

    public GenerateSalesReportService(InvoiceRepositoryPort invoiceRepository,
                                      RefundRepositoryPort refundRepository,
                                      OrderRepositoryPort orderRepository,
                                      ProductRepositoryPort productRepository,
                                      ValidateRoleAuthorizationService authorization) {
        this.invoiceRepository = invoiceRepository;
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.authorization = authorization;
    }

    public SalesReport generate(User actor) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);

        Money gross = Money.ZERO;
        Map<Long, Money> salesBySeller = new HashMap<>();
        for (Invoice invoice : invoiceRepository.findAll()) {
            gross = gross.add(invoice.getTotal());
            for (OrderItem line : invoice.getLines()) {
                Product product = productRepository.findById(line.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado."));
                Money current = salesBySeller.getOrDefault(product.getSellerId(), Money.ZERO);
                salesBySeller.put(product.getSellerId(), current.add(line.subtotal()));
            }
        }

        Money refunded = Money.ZERO;
        for (Refund refund : refundRepository.findAll()) {
            if (refund.getStatus() == RefundStatus.PROCESSED) {
                refunded = refunded.add(refund.getAmount());
            }
        }

        Map<OrderStatus, Integer> ordersByStatus = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(status, 0);
        }
        for (Order order : orderRepository.findAll()) {
            ordersByStatus.put(order.getStatus(), ordersByStatus.get(order.getStatus()) + 1);
        }

        return new SalesReport(gross, refunded, gross.subtract(refunded), ordersByStatus, salesBySeller);
    }
}