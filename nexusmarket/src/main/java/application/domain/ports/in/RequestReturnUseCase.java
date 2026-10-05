package application.domain.ports.in;

import application.domain.models.ReturnRequest;
import application.domain.models.User;

import java.util.Map;

public interface RequestReturnUseCase {

    ReturnRequest request(User actor, Command command);

    record Command(Long orderId, Map<Long, Integer> quantities, String reason) {
    }
}