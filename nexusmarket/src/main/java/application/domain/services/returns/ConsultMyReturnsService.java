package application.domain.services.returns;

import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultMyReturnsService {

    private final ReturnRepositoryPort returnRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultMyReturnsService(ReturnRepositoryPort returnRepository,
                                   ValidateRoleAuthorizationService authorization) {
        this.returnRepository = returnRepository;
        this.authorization = authorization;
    }

    public List<ReturnRequest> consultMyReturns(User actor) {
        authorization.requireRole(actor, UserRole.BUYER);
        return returnRepository.findByBuyerId(actor.getId());
    }
}