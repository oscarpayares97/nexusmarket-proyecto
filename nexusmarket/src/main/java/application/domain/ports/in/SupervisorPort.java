package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.SalesReport;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;

import java.util.List;

/** Rol de solo lectura: consulta y reportes, sin modificar nada. */
public interface SupervisorPort {

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
}