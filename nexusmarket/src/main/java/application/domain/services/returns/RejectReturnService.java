package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class RejectReturnService {

    private final ReturnRepositoryPort returnRepository;
    private final ValidateRoleAuthorizationService authorization;

    public RejectReturnService(ReturnRepositoryPort returnRepository,
                               ValidateRoleAuthorizationService authorization) {
        this.returnRepository = returnRepository;
        this.authorization = authorization;
    }

    public ReturnRequest reject(User actor, Long returnId, String reason) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);
        ReturnRequest returnRequest = returnRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Resultado no encontrado."));
        returnRequest.reject(reason);
        return returnRepository.save(returnRequest);
    }
}