package application.domain.ports.out;

import application.domain.models.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findOpenCartByBuyerId(Long buyerId);

    List<Order> findByBuyerId(Long buyerId);

    List<Order> findAll();
}