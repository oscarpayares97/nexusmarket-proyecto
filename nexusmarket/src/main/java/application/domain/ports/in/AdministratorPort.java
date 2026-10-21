package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;

public interface AdministratorPort {

    Seller registerSeller(User actor, RegisterSellerCommand command);

    Warehouse createWarehouse(User actor, CreateWarehouseCommand command);

    ReturnRequest approveReturn(User actor, Long returnId);

    ReturnRequest rejectReturn(User actor, Long returnId, String reason);

    Refund processRefund(User actor, Long refundId);

    record RegisterSellerCommand(String document, String fullName, String email, String rawPassword,
                                 String tradeName, String warehouseName, String warehouseLocation) {
    }

    record CreateWarehouseCommand(String name, String location, Long sellerId) {
    }
}