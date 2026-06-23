package com.university.inventory.service;

import com.university.inventory.dto.ProductRequest;
import com.university.inventory.exception.ProductNotFoundException;
import com.university.inventory.model.Product;
import com.university.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Product business logic.
 *
 * The service layer sits between controllers and repositories.
 * It contains all business rules and keeps controllers thin.
 *
 * @RequiredArgsConstructor (Lombok) generates a constructor for all final fields,
 * enabling constructor-based dependency injection (preferred over @Autowired).
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Retrieves all products from the database.
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a single product by ID.
     * Throws ProductNotFoundException if not found (handled by GlobalExceptionHandler).
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Creates a new product from the incoming request DTO.
     * Maps DTO fields to the Product entity and saves to DB.
     */
    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        return productRepository.save(product);
    }

    /**
     * Updates an existing product.
     * First checks the product exists, then updates all fields.
     * @Transactional ensures the operation is atomic.
     */
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product existing = getProductById(id); // throws if not found

        existing.setName(request.getName());
        existing.setCategory(request.getCategory());
        existing.setPrice(request.getPrice());
        existing.setQuantity(request.getQuantity());

        return productRepository.save(existing);
    }

    /**
     * Deletes a product by ID.
     * Verifies the product exists before deletion.
     */
    @Transactional
    public void deleteProduct(Long id) {
        // Verify existence before deleting to give a proper 404 error
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Searches products by name (case-insensitive partial match).
     * e.g., searching "lap" will match "Laptop", "laptop stand", etc.
     */
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
