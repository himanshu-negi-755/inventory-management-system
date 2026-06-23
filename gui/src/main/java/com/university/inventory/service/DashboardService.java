package com.university.inventory.service;

import com.university.inventory.dto.DashboardResponse;
import com.university.inventory.model.Product;
import com.university.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;

    /** Products with quantity below this value are considered "low stock" */
    private static final int LOW_STOCK_THRESHOLD = 5;

    /**
     * Builds and returns the dashboard summary.
     * - Total product count
     * - Total distinct categories
     * - List of low-stock products (quantity < 5)
     */
    public DashboardResponse getDashboardStats() {
        long totalProducts = productRepository.count();
        long totalCategories = productRepository.countDistinctCategories();
        List<Product> lowStockProducts = productRepository.findByQuantityLessThan(LOW_STOCK_THRESHOLD);

        return DashboardResponse.builder()
                .totalProducts(totalProducts)
                .totalCategories(totalCategories)
                .lowStockProducts(lowStockProducts)
                .lowStockCount(lowStockProducts.size())
                .build();
    }
}
