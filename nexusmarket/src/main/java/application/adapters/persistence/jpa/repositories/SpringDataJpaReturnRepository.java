package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ReturnRequestJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaReturnRepository extends JpaRepository<ReturnRequestJpaEntity, Long> {

    List<ReturnRequestJpaEntity> findByOrderId(Long orderId);

    List<ReturnRequestJpaEntity> findByBuyerId(Long buyerId);
}