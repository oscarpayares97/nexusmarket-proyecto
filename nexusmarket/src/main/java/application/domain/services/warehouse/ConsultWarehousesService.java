package application.domain.services.warehouse;

import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultWarehousesService {

    private final WarehouseRepositoryPort warehouseRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultWarehousesService(WarehouseRepositoryPort warehouseRepository,
                                    ValidateRoleAuthorizationService authorization) {
        this.warehouseRepository = warehouseRepository;
        this.authorization = authorization;
    }

    public List<Warehouse> consultWarehouses(User actor) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR, UserRole.LOGISTICS_OPERATOR,
                UserRole.SUPERVISOR, UserRole.SELLER);
        if (actor.getRole() == UserRole.SELLER) {
            return warehouseRepository.findBySellerId(actor.getId());
        }
        return warehouseRepository.findAll();
    }
}