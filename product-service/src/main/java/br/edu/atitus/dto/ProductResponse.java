package br.edu.atitus.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String brand,
        String model,
        int yearModel,
        BigDecimal basePrice,
        String baseCurrency,
        BigDecimal price,
        String targetCurrency,
        int mileage,
        String transmission,
        String fuelType,
        String category,
        String color,
        int stock,
        String description,
        String imageUrl,
        String plate
) {
}
