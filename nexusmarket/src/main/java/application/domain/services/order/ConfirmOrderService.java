package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.in.ConfirmOrderUseCase;
import application.domain.ports.in.ReserveInventoryUseCase;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.valueobjects.UserRole;

public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final BuyerRepositoryPort buyerRepository;
    private final ProductRepositoryPort productRepository;
    private final ReserveInventoryUseCase reserveInventory;
    private final ValidateRoleAuthorizationService authorization;

    public ConfirmOrderService(OrderRepositoryPort orderRepository,
                               BuyerRepositoryPort buyerRepository,
                               ProductRepositoryPort productRepository,
                               ReserveInventoryUseCase reserveInventory,
                               ValidateRoleAuthorizationService authorization) {
        this.orderRepository = orderRepository;
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
        this.reserveInventory = reserveInventory;
        this.authorization = authorization;
    }

    @Override
    public Order confirm(User actor, String deliveryAddress) {
        authorization.requireRole(actor, UserRole.BUYER);
        Buyer buyer = buyerRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado ningún comprador."));
        if (!buyer.canPurchase()) {
            throw new UnauthorizedOperationException("El comprador ha sido suspendido y no puede realizar pedidos.");
        }
        if (!buyer.hasAddress(deliveryAddress)) {
            throw new InvalidDataException("La dirección de entrega debe ser una de las direcciones registradas del comprador.");
        }
        Order cart = orderRepository.findOpenCartByBuyerId(actor.getId())
                .orElseThrow(() -> new InvalidDataException("El carrito está vacío."));
        if (cart.getItems().isEmpty()) {
            throw new InvalidDataException("El carrito está vacío.");
        }

        // 1) Validar todo antes de reservar
        for (OrderItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado."));
            if (!product.isPublished()) {
                throw new InvalidDataException("El producto '" + item.getProductName() + "' ya no está disponible.");
            }
            if (item.isPhysical() && !reserveInventory.hasStock(item.getProductId(), item.getQuantity())) {
                throw new InsufficientStockException("Existencias insuficientes de '" + item.getProductName() + "'.");
            }
        }

        // 2) Reservar el inventario de los productos físicos
        for (OrderItem item : cart.getItems()) {
            if (item.isPhysical()) {
                Long warehouseId = reserveInventory.reserve(actor, item.getProductId(), item.getQuantity());
                item.assignWarehouse(warehouseId);
            }
        }

        // 3) Confirmar el pedido
        cart.confirm(deliveryAddress);
        return orderRepository.save(cart);
    }
}