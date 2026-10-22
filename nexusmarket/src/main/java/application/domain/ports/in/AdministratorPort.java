package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.SalesReport;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;

import java.util.List;

public interface AdministratorPort {

    // ----- Altas y estados -----
    Seller registerSeller(User actor, RegisterSellerCommand command);

    User registerInternalUser(User actor, RegisterInternalUserCommand command);

    User changeUserStatus(User actor, Long userId, UserStatus newStatus);

    Buyer changeBuyerCommercialStatus(User actor, Long buyerUserId, CommercialStatus newStatus);

    Warehouse createWarehouse(User actor, CreateWarehouseCommand command);

    Warehouse changeWarehouseStatus(User actor, Long warehouseId, boolean active);

    // ----- Devoluciones y reembolsos -----
    ReturnRequest approveReturn(User actor, Long returnId);

    ReturnRequest rejectReturn(User actor, Long returnId, String reason);

    Refund processRefund(User actor, Long refundId);

    // ----- Consultas y reportes -----
    List<User> consultUsers(User actor);

    List<Warehouse> consultWarehouses(User actor);

    List<Inventory> consultInventory(User actor, Long productId);

    List<InventoryMovement> consultInventoryMovements(User actor);

    List<Order> consultOrders(User actor);

    List<Invoice> consultInvoices(User actor);

    Invoice consultInvoice(User actor, Long orderId);

    List<Shipment> consultShipments(User actor);

    Shipment consultShipment(User actor, Long orderId);

    List<ReturnRequest> consultReturns(User actor);

    List<Refund> consultRefunds(User actor);

    SalesReport generateSalesReport(User actor);

    void changePassword(User actor, String currentPassword, String newPassword);

    record RegisterSellerCommand(String document, String fullName, String email, String rawPassword,
                                 String tradeName, String warehouseName, String warehouseLocation) {
    }

    record CreateWarehouseCommand(String name, String location, Long sellerId) {
    }

    record RegisterInternalUserCommand(String document, String fullName, String email,
                                       String rawPassword, UserRole role) {
    }
}