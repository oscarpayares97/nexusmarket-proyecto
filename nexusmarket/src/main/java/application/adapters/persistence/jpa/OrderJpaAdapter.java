package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaOrderRepository;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OrderJpaAdapter implements OrderRepositoryPort {

    private final SpringDataJpaOrderRepository repository;

    public OrderJpaAdapter(SpringDataJpaOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = repository.save(OrderJpaMapper.toEntity(order));
        if (order.getId() == null) {
            order.assignId(saved.getId());
        }
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id).map(OrderJpaMapper::toDomain);
    }

    @Override
    public Optional<Order> findOpenCartByBuyerId(Long buyerId) {
        return repository.findFirstByBuyerIdAndStatus(buyerId, OrderStatus.CART.name())
                .map(OrderJpaMapper::toDomain);
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        return repository.findByBuyerId(buyerId).stream().map(OrderJpaMapper::toDomain).toList();
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream().map(OrderJpaMapper::toDomain).toList();
    }
}