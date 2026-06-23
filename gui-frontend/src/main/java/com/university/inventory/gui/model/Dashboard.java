package com.university.inventory.gui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Client-side model for the dashboard statistics response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dashboard {

    private long totalProducts;
    private long totalCategories;
    private int lowStockCount;
    private List<Product> lowStockProducts;

    public Dashboard() {}

    public long getTotalProducts()                        { return totalProducts; }
    public void setTotalProducts(long totalProducts)      { this.totalProducts = totalProducts; }

    public long getTotalCategories()                          { return totalCategories; }
    public void setTotalCategories(long totalCategories)      { this.totalCategories = totalCategories; }

    public int getLowStockCount()                         { return lowStockCount; }
    public void setLowStockCount(int lowStockCount)       { this.lowStockCount = lowStockCount; }

    public List<Product> getLowStockProducts()                        { return lowStockProducts; }
    public void setLowStockProducts(List<Product> lowStockProducts)   { this.lowStockProducts = lowStockProducts; }
}
