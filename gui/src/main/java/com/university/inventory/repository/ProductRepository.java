package com.university.inventory.repository;

import com.university.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Product entity.
 * Extends JpaRepository which provides built-in CRUD methods:
 * save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * Spring Data JPA automatically generates the implementation at runtime.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Custom query method: Spring Data JPA parses the method name and
     * generates SQL: SELECT * FROM products WHERE name LIKE '%keyword%'
     * Case-insensitive search for better user experience.
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Counts how many distinct categories exist.
     * Uses explicit JPQL because Spring Data JPA cannot derive
     * a no-arg COUNT DISTINCT query from the method name alone.
     */
    @Query("SELECT COUNT(DISTINCT p.category) FROM Product p")
    long countDistinctCategories();

    /**
     * Finds all products where quantity is less than the given threshold.
     * Used to identify low-stock products (threshold = 5).
     */
    List<Product> findByQuantityLessThan(int threshold);
}
