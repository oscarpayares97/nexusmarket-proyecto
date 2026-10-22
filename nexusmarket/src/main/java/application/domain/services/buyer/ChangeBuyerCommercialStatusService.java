package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Guard;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.UserRole;

public class ChangeBuyerCommercialStatusService {

    private final BuyerRepositoryPort buyerRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ChangeBuyerCommercialStatusService(BuyerRepositoryPort buyerRepository,
                                              ValidateRoleAuthorizationService authorization) {
        this.buyerRepository = buyerRepository;
        this.authorization = authorization;
    }

    public Buyer change(User actor, Long buyerUserId, CommercialStatus newStatus) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);
        Guard.requireNonNull(newStatus, "La situación comercial");
        Buyer buyer = buyerRepository.findByUserId(buyerUserId)
                .orElseThrow(() -> new EntityNotFoundException("Comprador no encontrado."));
        if (newStatus == CommercialStatus.ENABLED) {
            buyer.enable();
        } else {
            buyer.suspend();
        }
        return buyerRepository.save(buyer);
    }
}