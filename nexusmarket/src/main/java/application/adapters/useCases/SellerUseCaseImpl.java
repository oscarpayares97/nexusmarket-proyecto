package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.in.SellerPort;
import application.domain.services.catalog.PublishProductService;
import application.domain.services.catalog.RegisterProductService;
import application.domain.services.inventory.RegisterInventoryEntryService;

public class SellerUseCaseImpl implements SellerPort {

    private final RegisterProductService registerProductService;
    private final PublishProductService publishProductService;
    private final RegisterInventoryEntryService registerInventoryEntryService;

    public SellerUseCaseImpl(RegisterProductService registerProductService,
                             PublishProductService publishProductService,
                             RegisterInventoryEntryService registerInventoryEntryService) {
        this.registerProductService = registerProductService;
        this.publishProductService = publishProductService;
        this.registerInventoryEntryService = registerInventoryEntryService;
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
    public Inventory registerInventoryEntry(User actor, Long productId, Long warehouseId, int quantity) {
        return registerInventoryEntryService.register(actor, productId, warehouseId, quantity);
    }
}