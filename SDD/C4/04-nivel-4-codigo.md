# C4 Level 4 - Code

Only the classes that take part in the main flows are drawn.

## Purchase and billing

```mermaid
classDiagram
    class BuyerPort {
        <<interface>>
        +addItemToCart(actor, productId, quantity) Order
        +confirmOrder(actor, deliveryAddress) Order
        +payOrder(actor, orderId, paymentApproved) Order
    }
    class BuyerUseCaseImpl
    class PayOrderService {
        +pay(actor, orderId, paymentApproved) Order
    }
    class IssueInvoiceService {
        +issue(order, buyer) Invoice
    }
    class Order {
        +addItem(product, quantity)
        +confirm(address)
        +markPaid()
        +markShipped()
        +markDelivered()
        +total() Money
        +onlyDigital() boolean
    }
    class OrderItem
    class Invoice {
        +issueFor(order, buyer)$ Invoice
    }
    class OrderRepositoryPort {
        <<interface>>
    }
    class OrderJpaAdapter
    BuyerUseCaseImpl ..|> BuyerPort
    BuyerUseCaseImpl --> PayOrderService
    PayOrderService --> OrderRepositoryPort
    PayOrderService --> IssueInvoiceService
    PayOrderService ..> Order
    Order "1" *-- "many" OrderItem
    IssueInvoiceService ..> Invoice
    OrderJpaAdapter ..|> OrderRepositoryPort
```

## Shipping, returns and refunds

```mermaid
classDiagram
    class Shipment {
        +pack()
        +dispatch(carrier, trackingNumber)
        +markInTransit()
        +confirmDelivery()
    }
    class ReturnRequest {
        +approve()
        +reject(reason)
        +total() Money
    }
    class ReturnItem
    class Refund {
        +forReturn(returnRequest)$ Refund
    }
    class DispatchShipmentService
    class ConfirmDeliveryService
    class ReceiveReturnService
    class ProcessRefundService
    class RegisterInventorySaleService
    class RegisterInventoryReturnService
    ReturnRequest "1" *-- "many" ReturnItem
    DispatchShipmentService ..> Shipment
    DispatchShipmentService --> RegisterInventorySaleService
    ConfirmDeliveryService ..> Shipment
    ReceiveReturnService ..> ReturnRequest
    ReceiveReturnService --> RegisterInventoryReturnService
    ReceiveReturnService ..> Refund
    ProcessRefundService ..> Refund
```

## Authorization collaborators

```mermaid
classDiagram
    class ValidateRoleAuthorizationService {
        +requireRole(actor, roles...)
    }
    class ValidateOwnershipService {
        +requireOwner(actor, ownerUserId)
    }
    class AnyDomainService
    AnyDomainService --> ValidateRoleAuthorizationService
    AnyDomainService ..> ValidateOwnershipService : when the resource has an owner
```

Complete class tables are generated in `SDD/domain/Domain Model.md`, `SDD/domain/Domain Value Objects.md` and `SDD/domain/services/`.