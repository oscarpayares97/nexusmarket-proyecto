package application.domain.services.shipping;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.in.PackShipmentUseCase;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class PackShipmentService implements PackShipmentUseCase {

    private final ShipmentRepositoryPort shipmentRepository;
    private final ValidateRoleAuthorizationService authorization;

    public PackShipmentService(ShipmentRepositoryPort shipmentRepository,
                               ValidateRoleAuthorizationService authorization) {
        this.shipmentRepository = shipmentRepository;
        this.authorization = authorization;
    }

    @Override
    public Shipment pack(User actor, Long shipmentId) {
        authorization.requireRole(actor, UserRole.LOGISTICS_OPERATOR);
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el envío."));
        shipment.pack();
        return shipmentRepository.save(shipment);
    }
}