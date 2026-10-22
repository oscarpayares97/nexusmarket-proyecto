package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ChangeWarehouseStatusService {

    private final WarehouseRepositoryPort warehouseRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ChangeWarehouseStatusService(WarehouseRepositoryPort warehouseRepository,
                                        ValidateRoleAuthorizationService authorization) {
        this.warehouseRepository = warehouseRepository;
        this.authorization = authorization;
    }

    public Warehouse change(User actor, Long warehouseId, boolean active) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Almacen no encontrado."));
        if (active) {
            warehouse.activate();
        } else {
            warehouse.deactivate();
        }
        return warehouseRepository.save(warehouse);
    }
}