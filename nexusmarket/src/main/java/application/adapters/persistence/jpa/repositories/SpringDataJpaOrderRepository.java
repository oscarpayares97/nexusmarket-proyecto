package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaOrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    Optional<OrderJpaEntity> findFirstByBuyerIdAndStatus(Long buyerId, String status);

    List<OrderJpaEntity> findByBuyerId(Long buyerId);
}