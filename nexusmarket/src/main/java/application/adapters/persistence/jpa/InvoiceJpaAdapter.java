package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.adapters.persistence.jpa.mappers.InvoiceJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInvoiceRepository;
import application.domain.models.Invoice;
import application.domain.ports.out.InvoiceRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceJpaAdapter implements InvoiceRepositoryPort {

    private final SpringDataJpaInvoiceRepository repository;

    public InvoiceJpaAdapter(SpringDataJpaInvoiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceJpaEntity saved = repository.save(InvoiceJpaMapper.toEntity(invoice));
        if (invoice.getId() == null) {
            invoice.assignId(saved.getId());
        }
        return invoice;
    }

    @Override
    public Optional<Invoice> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId).map(InvoiceJpaMapper::toDomain);
    }

    @Override
    public List<Invoice> findAll() {
        return repository.findAll().stream().map(InvoiceJpaMapper::toDomain).toList();
    }
}