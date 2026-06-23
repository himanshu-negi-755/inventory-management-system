package com.university.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a product in the inventory.
 * Maps to the "products" table in MySQL.
 * Uses Lombok to auto-generate getters, setters, constructors, and toString.
 */
@Entity
@Table(name = "products")
@Data                   // Lombok: generates getters, setters, equals, hashCode, toString
@NoArgsConstructor      // Lombok: generates default constructor (required by JPA)
@AllArgsConstructor     // Lombok: generates all-args constructor
@Builder                // Lombok: enables builder pattern for clean object creation
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment primary key
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @Min(value = 0, message = "Price cannot be negative")
    @Column(nullable = false)
    private double price;

    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private int quantity;

    /**
     * One product can have many stock history records.
     * CascadeType.ALL means if a product is deleted, its history is also deleted.
     * mappedBy refers to the "product" field in StockHistory.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude   // Lombok: prevents infinite recursion in toString
    @JsonIgnore         // Jackson: prevents infinite recursion during JSON serialization
    private List<StockHistory> stockHistories = new ArrayList<>();
}
