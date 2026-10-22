package application.domain.services.billing;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ConsultInvoiceService {

    private final InvoiceRepositoryPort invoiceRepository;
    private final ValidateRoleAuthorizationService authorization;
    private final ValidateOwnershipService ownership;

    public ConsultInvoiceService(InvoiceRepositoryPort invoiceRepository,
                                 ValidateRoleAuthorizationService authorization,
                                 ValidateOwnershipService ownership) {
        this.invoiceRepository = invoiceRepository;
        this.authorization = authorization;
        this.ownership = ownership;
    }

    public Invoice consultInvoice(User actor, Long orderId) {
        authorization.requireRole(actor, UserRole.BUYER, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("El pedido no tiene factura."));
        if (actor.getRole() == UserRole.BUYER) {
            ownership.requireOwner(actor, invoice.getBuyerId());
        }
        return invoice;
    }
}