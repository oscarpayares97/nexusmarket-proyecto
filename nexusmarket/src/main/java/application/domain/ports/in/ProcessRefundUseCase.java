package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.User;

public interface ProcessRefundUseCase {

    Refund process(User actor, Long refundId);
}