package br.edu.atitus.dto;

import java.util.UUID;

public record OrderItemRequest(
        UUID productId,
        int quantity
) {
}
