package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.SellerPort;
import application.domain.services.catalog.AddProductVariantService;
import application.domain.services.catalog.DiscontinueProductService;
import application.domain.services.catalog.PublishProductService;
import application.domain.services.catalog.RegisterProductService;
import application.domain.services.catalog.SuspendProductService;
import application.domain.services.inventory.AdjustInventoryService;
import application.domain.services.inventory.ChangeInventoryStatusService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.RegisterInventoryEntryService;
import application.domain.services.user.ChangeUserPasswordService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.valueobjects.InventoryStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class SellerUseCaseImpl implements SellerPort {

    private final RegisterProductService registerProductService;
    private final PublishProductService publishProductService;
    private final AddProductVariantService addProductVariantService;
    private final SuspendProductService suspendProductService;
    private final DiscontinueProductService discontinueProductService;
    private final RegisterInventoryEntryService registerInventoryEntryService;
    private final AdjustInventoryService adjustInventoryService;
    private final ChangeInventoryStatusService changeInventoryStatusService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultWarehousesService consultWarehousesService;
    private final ChangeUserPasswordService changeUserPasswordService;

    public SellerUseCaseImpl(RegisterProductService registerProductService,
                             PublishProductService publishProductService,
                             AddProductVariantService addProductVariantService,
                             SuspendProductService suspendProductService,
                             DiscontinueProductService discontinueProductService,
                             RegisterInventoryEntryService registerInventoryEntryService,
                             AdjustInventoryService adjustInventoryService,
                             ChangeInventoryStatusService changeInventoryStatusService,
                             ConsultInventoryService consultInventoryService,
                             ConsultWarehousesService consultWarehousesService,
                             ChangeUserPasswordService changeUserPasswordService) {
        this.registerProductService = registerProductService;
        this.publishProductService = publishProductService;
        this.addProductVariantService = addProductVariantService;
        this.suspendProductService = suspendProductService;
        this.discontinueProductService = discontinueProductService;
        this.registerInventoryEntryService = registerInventoryEntryService;
        this.adjustInventoryService = adjustInventoryService;
        this.changeInventoryStatusService = changeInventoryStatusService;
        this.consultInventoryService = consultInventoryService;
        this.consultWarehousesService = consultWarehousesService;
        this.changeUserPasswordService = changeUserPasswordService;
    }

    @Override
    public Product registerProduct(User actor, RegisterProductCommand command) {
        return registerProductService.register(actor, command);
    }

    @Override
    public Product publishProduct(User actor, Long productId) {
        return publishProductService.publish(actor, productId);
    }

    @Override
    public Product addProductVariant(User actor, Long productId, String variant) {
        return addProductVariantService.add(actor, productId, variant);
    }

    @Override
    public Product suspendProduct(User actor, Long productId) {
        return suspendProductService.suspend(actor, productId);
    }

    @Override
    public Product discontinueProduct(User actor, Long productId) {
        return discontinueProductService.discontinue(actor, productId);
    }

    @Override
    public Inventory registerInventoryEntry(User actor, Long productId, Long warehouseId, int quantity) {
        return registerInventoryEntryService.register(actor, productId, warehouseId, quantity);
    }

    @Override
    public Inventory adjustInventory(User actor, Long productId, Long warehouseId, int newQuantity) {
        return adjustInventoryService.adjust(actor, productId, warehouseId, newQuantity);
    }

    @Override
    public Inventory changeInventoryStatus(User actor, Long productId, Long warehouseId,
                                           InventoryStatus status) {
        return changeInventoryStatusService.change(actor, productId, warehouseId, status);
    }

    @Override
    public List<Inventory> consultInventory(User actor, Long productId) {
        return consultInventoryService.consultInventory(actor, productId);
    }

    @Override
    public List<Warehouse> consultWarehouses(User actor) {
        return consultWarehousesService.consultWarehouses(actor);
    }

    @Override
    public void changePassword(User actor, String currentPassword, String newPassword) {
        changeUserPasswordService.change(actor, currentPassword, newPassword);
    }
}