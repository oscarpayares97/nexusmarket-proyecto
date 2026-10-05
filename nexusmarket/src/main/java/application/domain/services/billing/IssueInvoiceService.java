package application.domain.services.billing;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.in.IssueInvoiceUseCase;
import application.domain.ports.out.InvoiceRepositoryPort;

public class IssueInvoiceService implements IssueInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;

    public IssueInvoiceService(InvoiceRepositoryPort invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice issue(Order order, User buyer) {
        if (invoiceRepository.findByOrderId(order.getId()).isPresent()) {
            throw new DuplicateEntityException("Tu orden esta lista para ser facturada.");
        }
        return invoiceRepository.save(Invoice.issueFor(order, buyer));
    }
}