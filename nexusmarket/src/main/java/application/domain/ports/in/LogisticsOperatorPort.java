package application.domain.ports.in;

public interface LogisticsOperatorPort extends RegisterInventoryEntryUseCase,
        StartShipmentPreparationUseCase, PackShipmentUseCase, DispatchShipmentUseCase,
        RegisterShipmentInTransitUseCase, ConfirmDeliveryUseCase, ReceiveReturnUseCase {
}