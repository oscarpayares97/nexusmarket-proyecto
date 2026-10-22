package application.domain.services.shipping;

import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

import java.util.List;

public class ConsultShipmentsService {

    private final ShipmentRepositoryPort shipmentRepository;
    private final ValidateRoleAuthorizationService authorization;

    public ConsultShipmentsService(ShipmentRepositoryPort shipmentRepository,
                                   ValidateRoleAuthorizationService authorization) {
        this.shipmentRepository = shipmentRepository;
        this.authorization = authorization;
    }

    public List<Shipment> consultShipments(User actor) {
        authorization.requireRole(actor, UserRole.LOGISTICS_OPERATOR, UserRole.ADMINISTRATOR,
                UserRole.SUPERVISOR);
        return shipmentRepository.findAll();
    }
}