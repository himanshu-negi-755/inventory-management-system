package com.university.inventory.controller;

import com.university.inventory.dto.ProductRequest;
import com.university.inventory.model.Product;
import com.university.inventory.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product management.
 *
 * @RestController = @Controller + @ResponseBody (auto-serializes return values to JSON)
 * @RequestMapping sets the base URL path for all endpoints in this controller.
 *
 * Each method maps to a specific HTTP verb and path.
 * ResponseEntity allows us to control both the response body and HTTP status code.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /products
     * Returns a list of all products in the inventory.
     * HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * GET /products/{id}
     * Returns a single product by its ID.
     * HTTP 200 OK, or 404 if not found (handled by GlobalExceptionHandler).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * GET /products/search?name=keyword
     * Case-insensitive partial name search.
     * HTTP 200 OK with matching products (empty list if none found).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    /**
     * POST /products
     * Creates a new product.
     * @Valid triggers validation on ProductRequest fields.
     * HTTP 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        Product created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /products/{id}
     * Updates an existing product by ID.
     * HTTP 200 OK with updated product data.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    /**
     * DELETE /products/{id}
     * Deletes a product by ID (also cascades and deletes its stock history).
     * HTTP 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
