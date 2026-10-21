package application.domain.services.buyer;

import application.domain.models.Buyer;
import application.domain.ports.out.BuyerRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class InMemoryBuyerRepository implements BuyerRepositoryPort {

    private final Map<Long, Buyer> buyers = new HashMap<>();

    @Override
    public Buyer save(Buyer buyer) {
        buyers.put(buyer.getUserId(), buyer);
        return buyer;
    }

    @Override
    public Optional<Buyer> findByUserId(Long userId) {
        return Optional.ofNullable(buyers.get(userId));
    }
}