package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Refund;
import application.domain.models.User;
import application.domain.ports.in.ProcessRefundUseCase;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ProcessRefundService implements ProcessRefundUseCase {

    private final RefundRepositoryPort refundRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ProcessRefundService(RefundRepositoryPort refundRepository,
                                ValidateRoleAuthorizationService authorization) {
        this.refundRepository = refundRepository;
        this.authorization = authorization;
    }

    @Override
    public Refund process(User actor, Long refundId) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro resultado."));
        refund.process();
        return refundRepository.save(refund);
    }
}