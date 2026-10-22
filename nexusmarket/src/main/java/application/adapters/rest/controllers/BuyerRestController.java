package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.RequestDTOs.AddCartItemRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.AddressRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.ChangePasswordRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.ConfirmOrderRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.PayOrderRequest;
import application.adapters.rest.dtos.requests.RequestDTOs.RequestReturnRequest;
import application.adapters.rest.dtos.responses.ResponseDTOs.BuyerProfileResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.InvoiceResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.OrderResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ReturnResponse;
import application.adapters.rest.dtos.responses.ResponseDTOs.ShipmentResponse;
import application.adapters.rest.mappers.DomainRestMapper;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Operaciones del rol BUYER. SecurityConfig exige el rol para todo /api/v1/buyer/**. */
@RestController
@RequestMapping("/api/v1/buyer")
public class BuyerRestController {

    private final BuyerPort buyerPort;

    public BuyerRestController(BuyerPort buyerPort) {
        this.buyerPort = buyerPort;
    }

    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public BuyerProfileResponse addAddress(@AuthenticationPrincipal(expression = "user") User actor,
                                           @Valid @RequestBody AddressRequest request) {
        return DomainRestMapper.toBuyer(buyerPort.addAddress(actor, request.address()));
    }

    @PostMapping("/cart/items")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse addItemToCart(@AuthenticationPrincipal(expression = "user") User actor,
                                       @Valid @RequestBody AddCartItemRequest request) {
        return DomainRestMapper.toOrder(
                buyerPort.addItemToCart(actor, request.productId(), request.quantity()));
    }

    @DeleteMapping("/cart/items/{productId}")
    public OrderResponse removeItemFromCart(@AuthenticationPrincipal(expression = "user") User actor,
                                            @PathVariable Long productId) {
        return DomainRestMapper.toOrder(buyerPort.removeItemFromCart(actor, productId));
    }

    @PostMapping("/orders/confirm")
    public OrderResponse confirmOrder(@AuthenticationPrincipal(expression = "user") User actor,
                                      @Valid @RequestBody ConfirmOrderRequest request) {
        return DomainRestMapper.toOrder(buyerPort.confirmOrder(actor, request.deliveryAddress()));
    }

    @PostMapping("/orders/{orderId}/pay")
    public OrderResponse payOrder(@AuthenticationPrincipal(expression = "user") User actor,
                                  @PathVariable Long orderId,
                                  @Valid @RequestBody PayOrderRequest request) {
        return DomainRestMapper.toOrder(
                buyerPort.payOrder(actor, orderId, request.paymentApproved()));
    }

    @GetMapping("/orders")
    public List<OrderResponse> consultMyOrders(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(buyerPort.consultMyOrders(actor), DomainRestMapper::toOrder);
    }

    @GetMapping("/orders/{orderId}/invoice")
    public InvoiceResponse consultInvoice(@AuthenticationPrincipal(expression = "user") User actor,
                                          @PathVariable Long orderId) {
        return DomainRestMapper.toInvoice(buyerPort.consultInvoice(actor, orderId));
    }

    @GetMapping("/orders/{orderId}/shipment")
    public ShipmentResponse consultShipment(@AuthenticationPrincipal(expression = "user") User actor,
                                            @PathVariable Long orderId) {
        return DomainRestMapper.toShipment(buyerPort.consultShipment(actor, orderId));
    }

    @PostMapping("/returns")
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnResponse requestReturn(@AuthenticationPrincipal(expression = "user") User actor,
                                        @Valid @RequestBody RequestReturnRequest request) {
        BuyerPort.RequestReturnCommand command = new BuyerPort.RequestReturnCommand(
                request.orderId(), request.quantities(), request.reason());
        return DomainRestMapper.toReturn(buyerPort.requestReturn(actor, command));
    }

    @GetMapping("/returns")
    public List<ReturnResponse> consultMyReturns(@AuthenticationPrincipal(expression = "user") User actor) {
        return DomainRestMapper.mapList(buyerPort.consultMyReturns(actor), DomainRestMapper::toReturn);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal(expression = "user") User actor,
                               @Valid @RequestBody ChangePasswordRequest request) {
        buyerPort.changePassword(actor, request.currentPassword(), request.newPassword());
    }
}