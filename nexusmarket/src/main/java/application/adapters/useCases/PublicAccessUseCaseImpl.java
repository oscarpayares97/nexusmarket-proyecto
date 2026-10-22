package application.adapters.useCases;

import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.ports.in.PublicAccessPort;
import application.domain.services.buyer.RegisterBuyerService;
import application.domain.services.catalog.ConsultPublishedProductsService;
import application.domain.services.user.LoginService;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PublicAccessUseCaseImpl implements PublicAccessPort {

    private final RegisterBuyerService registerBuyerService;
    private final LoginService loginService;
    private final ConsultPublishedProductsService consultPublishedProductsService;

    public PublicAccessUseCaseImpl(RegisterBuyerService registerBuyerService,
                                   LoginService loginService,
                                   ConsultPublishedProductsService consultPublishedProductsService) {
        this.registerBuyerService = registerBuyerService;
        this.loginService = loginService;
        this.consultPublishedProductsService = consultPublishedProductsService;
    }

    @Override
    public Buyer registerBuyer(RegisterBuyerCommand command) {
        return registerBuyerService.register(command);
    }

    @Override
    public AuthenticationResult login(String email, String rawPassword) {
        return loginService.login(email, rawPassword);
    }

    @Override
    public List<Product> consultPublishedProducts() {
        return consultPublishedProductsService.consult();
    }
}