package application.domain.ports.in;

import application.domain.models.User;

public interface RegisterInventoryReturnUseCase {

    void registerReturn(User operator, Long productId, Long warehouseId, int quantity);
}