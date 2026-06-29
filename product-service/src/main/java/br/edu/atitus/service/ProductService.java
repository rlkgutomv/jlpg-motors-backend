package br.edu.atitus.service;

import br.edu.atitus.dto.CurrencyConversionResponse;
import br.edu.atitus.dto.ProductResponse;
import br.edu.atitus.model.ProductEntity;
import br.edu.atitus.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CurrencyConversionService currencyConversionService;

    public ProductService(ProductRepository productRepository, CurrencyConversionService currencyConversionService) {
        this.productRepository = productRepository;
        this.currencyConversionService = currencyConversionService;
    }

    @Cacheable(cacheNames = "products", key = "#targetCurrency")
    public List<ProductResponse> findAll(String targetCurrency) {
        return productRepository.findAll()
                .stream()
                .map(product -> toResponse(product, targetCurrency))
                .toList();
    }

    @Cacheable(cacheNames = "productById", key = "#id + '-' + #targetCurrency")
    public ProductResponse findById(UUID id, String targetCurrency) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto nao encontrado."));

        return toResponse(product, targetCurrency);
    }

    @CacheEvict(cacheNames = {"products", "productById"}, allEntries = true)
    public ProductResponse save(ProductEntity product) {
        normalizeProduct(product);

        if (product.getId() == null && productRepository.findByPlate(product.getPlate()).isPresent()) {
            throw new RuntimeException("Ja existe um produto cadastrado com esta placa.");
        }

        validateProductFields(product);
        ProductEntity savedProduct = productRepository.save(product);
        return toResponse(savedProduct, savedProduct.getBaseCurrency());
    }

    @CacheEvict(cacheNames = {"products", "productById"}, allEntries = true)
    public ProductResponse update(UUID id, ProductEntity updatedProduct) {
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto nao encontrado."));

        updatedProduct.setId(id);
        normalizeProduct(updatedProduct);

        if (!existingProduct.getPlate().equalsIgnoreCase(updatedProduct.getPlate())
                && productRepository.findByPlate(updatedProduct.getPlate()).isPresent()) {
            throw new RuntimeException("Ja existe um produto cadastrado com esta placa.");
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setModel(updatedProduct.getModel());
        existingProduct.setYearModel(updatedProduct.getYearModel());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setBaseCurrency(updatedProduct.getBaseCurrency());
        existingProduct.setMileage(updatedProduct.getMileage());
        existingProduct.setTransmission(updatedProduct.getTransmission());
        existingProduct.setFuelType(updatedProduct.getFuelType());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setColor(updatedProduct.getColor());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setPlate(updatedProduct.getPlate());

        validateProductFields(existingProduct);
        ProductEntity savedProduct = productRepository.save(existingProduct);
        return toResponse(savedProduct, savedProduct.getBaseCurrency());
    }

    @CacheEvict(cacheNames = {"products", "productById"}, allEntries = true)
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto nao encontrado para exclusao.");
        }

        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(ProductEntity product, String targetCurrency) {
        String currency = normalizeCurrency(targetCurrency, product.getBaseCurrency());
        CurrencyConversionResponse conversion = currencyConversionService.convert(
                product.getBaseCurrency(),
                currency,
                product.getPrice()
        );

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getModel(),
                product.getYearModel(),
                product.getPrice(),
                product.getBaseCurrency(),
                conversion.getConvertedAmount(),
                conversion.getTarget(),
                product.getMileage(),
                product.getTransmission(),
                product.getFuelType(),
                product.getCategory(),
                product.getColor(),
                product.getStock(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPlate()
        );
    }

    private void normalizeProduct(ProductEntity product) {
        if (product.getPlate() != null) {
            product.setPlate(product.getPlate().trim().toUpperCase());
        }
        if (product.getBaseCurrency() == null || product.getBaseCurrency().trim().isEmpty()) {
            product.setBaseCurrency("USD");
        } else {
            product.setBaseCurrency(product.getBaseCurrency().trim().toUpperCase());
        }
    }

    private String normalizeCurrency(String targetCurrency, String fallbackCurrency) {
        if (targetCurrency == null || targetCurrency.trim().isEmpty()) {
            return fallbackCurrency;
        }
        return targetCurrency.trim().toUpperCase();
    }

    private void validateProductFields(ProductEntity product) {
        if (isBlank(product.getName())) {
            throw new RuntimeException("O nome do produto e obrigatorio.");
        }
        if (isBlank(product.getBrand())) {
            throw new RuntimeException("A marca do produto e obrigatoria.");
        }
        if (isBlank(product.getModel())) {
            throw new RuntimeException("O modelo do produto e obrigatorio.");
        }
        if (product.getYearModel() < 1900) {
            throw new RuntimeException("O ano do produto deve ser maior ou igual a 1900.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O preco do produto deve ser maior que zero.");
        }
        if (isBlank(product.getBaseCurrency()) || product.getBaseCurrency().length() != 3) {
            throw new RuntimeException("A moeda base deve ter 3 letras.");
        }
        if (isBlank(product.getTransmission())) {
            throw new RuntimeException("O tipo de cambio e obrigatorio.");
        }
        if (isBlank(product.getFuelType())) {
            throw new RuntimeException("O tipo de combustivel e obrigatorio.");
        }
        if (isBlank(product.getCategory())) {
            throw new RuntimeException("A categoria e obrigatoria.");
        }
        if (isBlank(product.getColor())) {
            throw new RuntimeException("A cor e obrigatoria.");
        }
        if (product.getStock() < 0) {
            throw new RuntimeException("A quantidade em estoque nao pode ser negativa.");
        }
        if (isBlank(product.getPlate())) {
            throw new RuntimeException("A placa do produto e obrigatoria.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
