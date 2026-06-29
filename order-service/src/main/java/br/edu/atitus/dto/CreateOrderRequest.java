package br.edu.atitus.dto;

import java.util.List;

public record CreateOrderRequest(
        String targetCurrency,
        List<OrderItemRequest> items
) {
}
