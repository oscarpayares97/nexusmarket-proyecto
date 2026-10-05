package application.domain.ports.in;

import application.domain.models.ReturnRequest;
import application.domain.models.User;

public interface RejectReturnUseCase {

    ReturnRequest reject(User actor, Long returnId, String reason);
}