package application.domain.ports.in;

public interface BuyerPort extends AddItemToCartUseCase, ConfirmOrderUseCase,
        PayOrderUseCase, RequestReturnUseCase {
}