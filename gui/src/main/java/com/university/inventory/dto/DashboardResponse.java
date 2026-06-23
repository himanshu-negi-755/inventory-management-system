package com.university.inventory.dto;

import com.university.inventory.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO returned by the /dashboard endpoint.
 * Aggregates summary statistics for the inventory dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /** Total number of products in the inventory */
    private long totalProducts;

    /** Number of distinct product categories */
    private long totalCategories;

    /** Products with quantity below the low-stock threshold (< 5) */
    private List<Product> lowStockProducts;

    /** Count of low-stock products for quick display */
    private int lowStockCount;
}
