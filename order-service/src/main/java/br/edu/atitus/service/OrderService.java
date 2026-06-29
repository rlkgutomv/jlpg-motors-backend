package br.edu.atitus.service;

import br.edu.atitus.dto.CreateOrderRequest;
import br.edu.atitus.dto.CurrencyConversionResponse;
import br.edu.atitus.dto.OrderItemRequest;
import br.edu.atitus.dto.OrderItemResponse;
import br.edu.atitus.dto.OrderResponse;
import br.edu.atitus.dto.ProductResponse;
import br.edu.atitus.model.OrderEntity;
import br.edu.atitus.model.OrderItemEntity;
import br.edu.atitus.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductLookupService productLookupService;
    private final OrderCurrencyConversionService currencyConversionService;

    public OrderService(OrderRepository orderRepository, ProductLookupService productLookupService,
                        OrderCurrencyConversionService currencyConversionService) {
        this.orderRepository = orderRepository;
        this.productLookupService = productLookupService;
        this.currencyConversionService = currencyConversionService;
    }

    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        validateRequest(request);
        String targetCurrency = normalizeCurrency(request.targetCurrency(), "BRL");

        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setCurrency(targetCurrency);
        order.setStatus("CREATED");

        BigDecimal orderTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            if (itemRequest.quantity() <= 0) {
                throw new RuntimeException("Quantidade do item deve ser maior que zero.");
            }

            ProductResponse product = productLookupService.findProduct(itemRequest.productId(), targetCurrency);
            BigDecimal baseLineTotal = product.basePrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            CurrencyConversionResponse conversion = currencyConversionService.convert(
                    product.baseCurrency(),
                    targetCurrency,
                    baseLineTotal
            );

            BigDecimal lineTotal = conversion.convertedAmount();
            BigDecimal unitPrice = lineTotal.divide(BigDecimal.valueOf(itemRequest.quantity()), 2, RoundingMode.HALF_UP);

            OrderItemEntity item = new OrderItemEntity();
            item.setProductId(product.id());
            item.setProductName(product.name());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(lineTotal);
            item.setCurrency(targetCurrency);

            order.addItem(item);
            orderTotal = orderTotal.add(lineTotal);
        }

        order.setTotalAmount(orderTotal.setScale(2, RoundingMode.HALF_UP));
        OrderEntity savedOrder = orderRepository.save(order);
        return toResponse(savedOrder, targetCurrency);
    }

    public List<OrderResponse> findOrdersByUser(UUID userId, String currency) {
        String displayCurrency = normalizeCurrency(currency, "BRL");

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(order -> toResponse(order, displayCurrency))
                .toList();
    }

    private OrderResponse toResponse(OrderEntity order, String displayCurrency) {
        CurrencyConversionResponse conversion = currencyConversionService.convert(
                order.getCurrency(),
                displayCurrency,
                order.getTotalAmount()
        );

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        item.getCurrency()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                conversion.convertedAmount(),
                conversion.target(),
                order.getCreatedAt(),
                itemResponses
        );
    }

    private void validateRequest(CreateOrderRequest request) {
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new RuntimeException("Pedido deve conter ao menos um item.");
        }
    }

    private String normalizeCurrency(String currency, String fallback) {
        if (currency == null || currency.trim().isEmpty()) {
            return fallback;
        }

        String normalized = currency.trim().toUpperCase();
        if (normalized.length() != 3) {
            throw new RuntimeException("Moeda deve seguir o padrao ISO de 3 letras.");
        }

        return normalized;
    }
}
