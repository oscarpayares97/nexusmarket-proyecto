package application.domain.ports.in;

import application.domain.models.ReturnRequest;
import application.domain.models.User;

public interface ApproveReturnUseCase {

    ReturnRequest approve(User actor, Long returnId);
}