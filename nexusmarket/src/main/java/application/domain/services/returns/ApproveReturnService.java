package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ApproveReturnService {

    private final ReturnRepositoryPort returnRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ApproveReturnService(ReturnRepositoryPort returnRepository,
                                ValidateRoleAuthorizationService authorization) {
        this.returnRepository = returnRepository;
        this.authorization = authorization;
    }

    public ReturnRequest approve(User actor, Long returnId) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);
        ReturnRequest returnRequest = returnRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado ningún resultado."));
        returnRequest.approve();
        return returnRepository.save(returnRequest);
    }
}