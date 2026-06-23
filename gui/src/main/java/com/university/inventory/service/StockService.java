package com.university.inventory.service;

import com.university.inventory.dto.StockRequest;
import com.university.inventory.exception.InsufficientStockException;
import com.university.inventory.model.Product;
import com.university.inventory.model.StockHistory;
import com.university.inventory.model.StockHistory.OperationType;
import com.university.inventory.repository.ProductRepository;
import com.university.inventory.repository.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Stock operations.
 *
 * Handles ADD and REMOVE stock operations.
 * Each operation:
 *  1. Validates the product exists
 *  2. Checks business rules (e.g., sufficient stock for REMOVE)
 *  3. Updates product quantity
 *  4. Records the operation in stock_history table
 *
 * @Transactional ensures that both the product update and history insert
 * happen together — if one fails, neither is committed (atomicity).
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final ProductService productService;

    /**
     * Adds stock to a product and records the event in history.
     */
    @Transactional
    public StockHistory addStock(StockRequest request) {
        // Fetch product (throws ProductNotFoundException if missing)
        Product product = productService.getProductById(request.getProductId());

        // Increase quantity
        product.setQuantity(product.getQuantity() + request.getQuantity());
        productRepository.save(product);

        // Record the ADD operation in history
        return recordHistory(product, OperationType.ADD, request.getQuantity());
    }

    /**
     * Removes stock from a product and records the event in history.
     * Throws InsufficientStockException if requested quantity exceeds available stock.
     */
    @Transactional
    public StockHistory removeStock(StockRequest request) {
        Product product = productService.getProductById(request.getProductId());

        // Business rule: cannot remove more than available
        if (request.getQuantity() > product.getQuantity()) {
            throw new InsufficientStockException(
                    product.getName(),
                    product.getQuantity(),
                    request.getQuantity()
            );
        }

        // Decrease quantity
        product.setQuantity(product.getQuantity() - request.getQuantity());
        productRepository.save(product);

        // Record the REMOVE operation in history
        return recordHistory(product, OperationType.REMOVE, request.getQuantity());
    }

    /**
     * Returns all stock history records, sorted newest first.
     */
    public List<StockHistory> getAllHistory() {
        return stockHistoryRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .toList();
    }

    /**
     * Returns stock history for a specific product.
     */
    public List<StockHistory> getHistoryByProduct(Long productId) {
        return stockHistoryRepository.findByProductIdOrderByTimestampDesc(productId);
    }

    /**
     * Helper: creates and saves a StockHistory record.
     */
    private StockHistory recordHistory(Product product, OperationType operation, int quantity) {
        StockHistory history = StockHistory.builder()
                .product(product)
                .operation(operation)
                .quantity(quantity)
                .build();
        // timestamp is set automatically by @PrePersist in StockHistory entity
        return stockHistoryRepository.save(history);
    }
}
