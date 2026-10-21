package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;
import application.domain.ports.in.AdministratorPort.CreateWarehouseCommand;
public class CreateWarehouseService {

    private final WarehouseRepositoryPort warehouseRepository;
    private final SellerRepositoryPort sellerRepository;
    private final ValidateRoleAuthorizationService authorization;

    public CreateWarehouseService(WarehouseRepositoryPort warehouseRepository,
                                  SellerRepositoryPort sellerRepository,
                                  ValidateRoleAuthorizationService authorization) {
        this.warehouseRepository = warehouseRepository;
        this.sellerRepository = sellerRepository;
        this.authorization = authorization;
    }

    public Warehouse create(User actor, CreateWarehouseCommand command) {
        authorization.requireRole(actor, UserRole.ADMINISTRATOR);
        WarehouseType type = WarehouseType.MARKETPLACE;
        if (command.sellerId() != null) {
            sellerRepository.findByUserId(command.sellerId())
                    .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el vendedor."));
            type = WarehouseType.SELLER;
        }
        return warehouseRepository.save(
                new Warehouse(null, command.name(), command.location(), type, command.sellerId()));
    }
}