package com.university.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for receiving product data from the client.
 *
 * Why use a DTO instead of the entity directly?
 * - Prevents over-posting attacks (client cannot set sensitive fields like id)
 * - Decouples the API contract from the database schema
 * - Validation annotations here only apply to incoming requests
 */
@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 0, message = "Price must be zero or positive")
    private double price;

    @Min(value = 0, message = "Quantity must be zero or positive")
    private int quantity;
}
