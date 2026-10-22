package application.domain.services.inventory;

import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultInventoryMovementsService {

    private final InventoryRepositoryPort inventoryRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultInventoryMovementsService(InventoryRepositoryPort inventoryRepository,
                                            ValidateRoleAuthorizationService authorization) {
        this.inventoryRepository = inventoryRepository;
        this.authorization = authorization;
    }

    public List<InventoryMovement> consultMovements(User actor) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR, UserRole.SUPERVISOR,
                UserRole.LOGISTICS_OPERATOR);
        return inventoryRepository.findAllMovements();
    }
}