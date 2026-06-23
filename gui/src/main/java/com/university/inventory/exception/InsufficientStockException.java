package com.university.inventory.exception;

/**
 * Custom exception thrown when trying to remove more stock than available.
 * This prevents the quantity from going negative.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, int available, int requested) {
        super(String.format(
            "Insufficient stock for '%s'. Available: %d, Requested: %d",
            productName, available, requested
        ));
    }
}
