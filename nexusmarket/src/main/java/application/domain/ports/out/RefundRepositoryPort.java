package application.domain.ports.out;

import application.domain.models.Refund;

import java.util.List;
import java.util.Optional;

public interface RefundRepositoryPort {

    Refund save(Refund refund);

    Optional<Refund> findById(Long id);

    List<Refund> findAll();
}