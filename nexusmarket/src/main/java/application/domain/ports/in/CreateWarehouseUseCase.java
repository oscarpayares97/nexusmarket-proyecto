package application.domain.ports.in;

import application.domain.models.User;
import application.domain.models.Warehouse;

public interface CreateWarehouseUseCase {

    Warehouse create(User actor, Command command);

    record Command(String name, String location, Long sellerId) {
    }
}