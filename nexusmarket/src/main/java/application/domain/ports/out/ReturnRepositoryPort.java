package application.domain.ports.out;

import application.domain.models.ReturnRequest;

import java.util.List;
import java.util.Optional;

public interface ReturnRepositoryPort {

    ReturnRequest save(ReturnRequest returnRequest);

    Optional<ReturnRequest> findById(Long id);

    List<ReturnRequest> findByOrderId(Long orderId);

    List<ReturnRequest> findAll();
}