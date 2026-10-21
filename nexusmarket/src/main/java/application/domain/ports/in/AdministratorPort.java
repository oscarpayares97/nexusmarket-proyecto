package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;

import java.util.List;

public interface AdministratorPort {

    Seller registerSeller(User actor, RegisterSellerCommand command);

    Warehouse createWarehouse(User actor, CreateWarehouseCommand command);

    User registerInternalUser(User actor, RegisterInternalUserCommand command);

    User changeUserStatus(User actor, Long userId, UserStatus newStatus);

    List<User> consultUsers(User actor);

    ReturnRequest approveReturn(User actor, Long returnId);

    ReturnRequest rejectReturn(User actor, Long returnId, String reason);

    Refund processRefund(User actor, Long refundId);

    record RegisterSellerCommand(String document, String fullName, String email, String rawPassword,
                                 String tradeName, String warehouseName, String warehouseLocation) {
    }

    record CreateWarehouseCommand(String name, String location, Long sellerId) {
    }

    record RegisterInternalUserCommand(String document, String fullName, String email,
                                       String rawPassword, UserRole role) {
    }
}