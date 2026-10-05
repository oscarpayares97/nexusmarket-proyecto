package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDataException;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.in.AddItemToCartUseCase;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class AddItemToCartService implements AddItemToCartUseCase {

    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final ValidateRoleAuthorizationService authorization;

    public AddItemToCartService(OrderRepositoryPort orderRepository,
                                ProductRepositoryPort productRepository,
                                ValidateRoleAuthorizationService authorization) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.authorization = authorization;
    }

    @Override
    public Order add(User actor, Long productId, int quantity) {
        authorization.requireRole(actor, UserRole.BUYER);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado."));
        if (!product.isPublished()) {
            throw new InvalidDataException("El producto no esta publicado.");
        }
        // Cada comprador tiene un carrito abierto: se reutiliza o se crea
        Order cart = orderRepository.findOpenCartByBuyerId(actor.getId())
                .orElseGet(() -> new Order(null, actor.getId()));
        cart.addItem(product, quantity);
        return orderRepository.save(cart);
    }
}