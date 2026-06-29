package br.edu.atitus.controller;

import br.edu.atitus.dto.CreateOrderRequest;
import br.edu.atitus.dto.OrderResponse;
import br.edu.atitus.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/ws/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(UUID.fromString(userId), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/ws/orders/{currency}")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String currency) {
        return ResponseEntity.ok(orderService.findOrdersByUser(UUID.fromString(userId), currency));
    }
}
