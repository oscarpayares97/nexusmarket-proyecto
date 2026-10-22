package application.domain.services.returns;

import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultReturnsService {

    private final ReturnRepositoryPort returnRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultReturnsService(ReturnRepositoryPort returnRepository,
                                 ValidateRoleAuthorizationService authorization) {
        this.returnRepository = returnRepository;
        this.authorization = authorization;
    }

    public List<ReturnRequest> consultReturns(User actor) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR,
                UserRole.LOGISTICS_OPERATOR);
        return returnRepository.findAll();
    }
}