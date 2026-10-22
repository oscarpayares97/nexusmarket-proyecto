package application.domain.services.billing;

import application.domain.models.Invoice;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultInvoicesService {

    private final InvoiceRepositoryPort invoiceRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultInvoicesService(InvoiceRepositoryPort invoiceRepository,
                                  ValidateRoleAuthorizationService authorization) {
        this.invoiceRepository = invoiceRepository;
        this.authorization = authorization;
    }

    public List<Invoice> consultInvoices(User actor) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        return invoiceRepository.findAll();
    }
}