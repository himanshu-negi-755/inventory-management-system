package com.university.inventory.gui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Client-side model for a stock history record returned by the API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockHistory {

    private Long id;
    private Product product;
    private String operation;
    private int quantity;
    private String timestamp;

    public StockHistory() {}

    public Long getId()                   { return id; }
    public void setId(Long id)            { this.id = id; }

    public Product getProduct()                   { return product; }
    public void setProduct(Product product)       { this.product = product; }

    public String getOperation()                  { return operation; }
    public void setOperation(String operation)    { this.operation = operation; }

    public int getQuantity()                      { return quantity; }
    public void setQuantity(int quantity)         { this.quantity = quantity; }

    public String getTimestamp()                  { return timestamp; }
    public void setTimestamp(String timestamp)    { this.timestamp = timestamp; }
}
