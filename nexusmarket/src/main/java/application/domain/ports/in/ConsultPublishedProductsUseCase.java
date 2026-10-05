package application.domain.ports.in;

import application.domain.models.Product;

import java.util.List;

public interface ConsultPublishedProductsUseCase {

    List<Product> consult();
}