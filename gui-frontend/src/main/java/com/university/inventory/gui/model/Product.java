package com.university.inventory.gui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Client-side model mirroring the backend Product entity.
 * @JsonIgnoreProperties(ignoreUnknown=true) safely ignores any extra fields
 * the API may return that we don't need in the frontend.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private Long id;
    private String name;
    private String category;
    private double price;
    private int quantity;

    public Product() {}

    public Product(String name, String category, double price, int quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId()           { return id; }
    public void setId(Long id)    { this.id = id; }

    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }

    public String getCategory()               { return category; }
    public void setCategory(String category)  { this.category = category; }

    public double getPrice()              { return price; }
    public void setPrice(double price)    { this.price = price; }

    public int getQuantity()              { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() { return name; }
}
