package com.university.inventory.controller;

import com.university.inventory.dto.StockRequest;
import com.university.inventory.model.StockHistory;
import com.university.inventory.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Stock management.
 *
 * Handles adding/removing stock and retrieving stock history.
 * All stock changes are recorded in the stock_history table automatically.
 */
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * POST /stock/add
     * Adds a specified quantity to an existing product.
     * Request body: { "productId": 1, "quantity": 10 }
     * HTTP 200 OK with the created StockHistory record.
     */
    @PostMapping("/add")
    public ResponseEntity<StockHistory> addStock(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(stockService.addStock(request));
    }

    /**
     * POST /stock/remove
     * Removes a specified quantity from an existing product.
     * Returns 400 if quantity requested exceeds available stock.
     * HTTP 200 OK with the created StockHistory record.
     */
    @PostMapping("/remove")
    public ResponseEntity<StockHistory> removeStock(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(stockService.removeStock(request));
    }

    /**
     * GET /stock/history
     * Returns all stock history records sorted by timestamp (newest first).
     */
    @GetMapping("/history")
    public ResponseEntity<List<StockHistory>> getAllHistory() {
        return ResponseEntity.ok(stockService.getAllHistory());
    }

    /**
     * GET /stock/history/{productId}
     * Returns stock history for a specific product.
     */
    @GetMapping("/history/{productId}")
    public ResponseEntity<List<StockHistory>> getHistoryByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getHistoryByProduct(productId));
    }
}
