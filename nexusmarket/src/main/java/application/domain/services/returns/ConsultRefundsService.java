package application.domain.services.returns;

import application.domain.models.Refund;
import application.domain.models.User;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultRefundsService {

    private final RefundRepositoryPort refundRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultRefundsService(RefundRepositoryPort refundRepository,
                                 ValidateRoleAuthorizationService authorization) {
        this.refundRepository = refundRepository;
        this.authorization = authorization;
    }

    public List<Refund> consultRefunds(User actor) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR);
        return refundRepository.findAll();
    }
}