package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.User;

public interface PublishProductUseCase {

    Product publish(User actor, Long productId);
}