package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Refund;
import application.domain.models.ReturnItem;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.in.ReceiveReturnUseCase;
import application.domain.ports.in.RegisterInventoryReturnUseCase;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ReceiveReturnService implements ReceiveReturnUseCase {

    private final ReturnRepositoryPort returnRepository;
    private final RefundRepositoryPort refundRepository;
    private final RegisterInventoryReturnUseCase inventoryReturn;
    private final ValidateRoleAuthorizationService authorization;

    public ReceiveReturnService(ReturnRepositoryPort returnRepository,
                                RefundRepositoryPort refundRepository,
                                RegisterInventoryReturnUseCase inventoryReturn,
                                ValidateRoleAuthorizationService authorization) {
        this.returnRepository = returnRepository;
        this.refundRepository = refundRepository;
        this.inventoryReturn = inventoryReturn;
        this.authorization = authorization;
    }

    @Override
    public ReturnRequest receive(User actor, Long returnId) {
        authorization.requireRole(actor, UserRole.LOGISTICS_OPERATOR);
        ReturnRequest returnRequest = returnRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro resultado."));

        returnRequest.markReceived();          // lanza error si no estaba aprobada
        for (ReturnItem item : returnRequest.getItems()) {
            inventoryReturn.registerReturn(actor, item.getProductId(), item.getWarehouseId(), item.getQuantity());
        }
        ReturnRequest saved = returnRepository.save(returnRequest);
        refundRepository.save(Refund.forReturn(saved));
        return saved;
    }
}