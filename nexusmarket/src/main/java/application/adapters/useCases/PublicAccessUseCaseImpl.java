package application.adapters.useCases;

import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.ports.in.PublicAccessPort;
import application.domain.services.buyer.RegisterBuyerService;
import application.domain.services.catalog.ConsultPublishedProductsService;

import java.util.List;

public class PublicAccessUseCaseImpl implements PublicAccessPort {

    private final RegisterBuyerService registerBuyerService;
    private final ConsultPublishedProductsService consultPublishedProductsService;

    public PublicAccessUseCaseImpl(RegisterBuyerService registerBuyerService,
                                   ConsultPublishedProductsService consultPublishedProductsService) {
        this.registerBuyerService = registerBuyerService;
        this.consultPublishedProductsService = consultPublishedProductsService;
    }

    @Override
    public Buyer registerBuyer(RegisterBuyerCommand command) {
        return registerBuyerService.register(command);
    }

    @Override
    public List<Product> consultPublishedProducts() {
        return consultPublishedProductsService.consult();
    }
}