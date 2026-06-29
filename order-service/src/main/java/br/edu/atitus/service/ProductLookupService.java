package br.edu.atitus.service;

import br.edu.atitus.client.ProductClient;
import br.edu.atitus.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ProductLookupService {

    private final ProductClient productClient;

    public ProductLookupService(ProductClient productClient) {
        this.productClient = productClient;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    public ProductResponse findProduct(UUID productId, String targetCurrency) {
        return productClient.findById(productId, targetCurrency);
    }

    public ProductResponse productFallback(UUID productId, String targetCurrency, Throwable ex) {
        throw new RuntimeException("Nao foi possivel consultar o produto " + productId + ".");
    }
}
