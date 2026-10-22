package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class AddBuyerAddressService {

    private final BuyerRepositoryPort buyerRepository;
    private final ValidateRoleAuthorizationService authorization;

    public AddBuyerAddressService(BuyerRepositoryPort buyerRepository,
                                  ValidateRoleAuthorizationService authorization) {
        this.buyerRepository = buyerRepository;
        this.authorization = authorization;
    }

    public Buyer add(User actor, String address) {
        authorization.requireRole(actor, UserRole.BUYER);
        Buyer buyer = buyerRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comprador no encontrado."));
        buyer.addAddress(address);
        return buyerRepository.save(buyer);
    }
}