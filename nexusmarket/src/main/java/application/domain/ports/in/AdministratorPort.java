package application.domain.ports.in;

public interface AdministratorPort extends RegisterSellerUseCase, CreateWarehouseUseCase,
        ApproveReturnUseCase, RejectReturnUseCase, ProcessRefundUseCase {
}