package com.university.inventory.exception;

/**
 * Custom exception thrown when a product with a given ID does not exist.
 * Extends RuntimeException so it does not need to be declared in method signatures.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found with ID: " + id);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
