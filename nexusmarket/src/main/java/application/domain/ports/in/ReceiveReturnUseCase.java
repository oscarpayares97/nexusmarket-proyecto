package application.domain.ports.in;

import application.domain.models.ReturnRequest;
import application.domain.models.User;

public interface ReceiveReturnUseCase {

    ReturnRequest receive(User actor, Long returnId);
}