package application.domain.models;

import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;

import java.util.Map;

public record SalesReport(Money grossSales, Money refunded, Money netSales,
                          Map<OrderStatus, Integer> ordersByStatus,
                          Map<Long, Money> salesBySeller) {
}