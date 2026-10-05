package application.domain.ports.in;

import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;

public interface IssueInvoiceUseCase {

    Invoice issue(Order order, User buyer);
}