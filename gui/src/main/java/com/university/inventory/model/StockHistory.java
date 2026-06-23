package com.university.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity that tracks every stock ADD or REMOVE operation.
 * Maps to the "stock_history" table in MySQL.
 * Each record links back to a specific Product via a foreign key.
 */
@Entity
@Table(name = "stock_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ManyToOne: many history records can belong to one product.
     * @JoinColumn creates the foreign key column "product_id" in this table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @JsonIgnoreProperties({"stockHistories", "hibernateLazyInitializer", "handler"}) // prevents circular ref and Hibernate proxy serialization errors
    private Product product;

    /**
     * Operation type: either ADD or REMOVE.
     * Stored as a String in the DB for readability.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType operation;

    @Column(nullable = false)
    private int quantity;

    /**
     * Automatically set to current time when the record is created.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * @PrePersist runs this method automatically before saving to DB.
     * Ensures timestamp is never null.
     */
    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Enum defining the two types of stock operations.
     */
    public enum OperationType {
        ADD, REMOVE
    }
}
