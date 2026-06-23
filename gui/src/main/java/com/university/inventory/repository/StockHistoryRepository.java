package com.university.inventory.repository;

import com.university.inventory.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for StockHistory entity.
 * Provides CRUD operations and a custom finder for history by product.
 */
@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    /**
     * Retrieves all stock history entries for a specific product,
     * ordered by timestamp descending (most recent first).
     */
    List<StockHistory> findByProductIdOrderByTimestampDesc(Long productId);
}
