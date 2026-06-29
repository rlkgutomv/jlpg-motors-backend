package br.edu.atitus.controller;

import br.edu.atitus.dto.ProductResponse;
import br.edu.atitus.model.ProductEntity;
import br.edu.atitus.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "BRL") String targetCurrency) {
        return ResponseEntity.ok(productService.findAll(targetCurrency));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "BRL") String targetCurrency) {
        return ResponseEntity.ok(productService.findById(id, targetCurrency));
    }

    @PostMapping("/ws/product")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductEntity product) {
        ProductResponse savedProduct = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping("/ws/product/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductEntity product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @DeleteMapping("/ws/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
