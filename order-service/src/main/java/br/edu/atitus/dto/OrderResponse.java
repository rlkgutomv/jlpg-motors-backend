package br.edu.atitus.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        String status,
        BigDecimal totalAmount,
        String currency,
        BigDecimal displayTotalAmount,
        String displayCurrency,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}
