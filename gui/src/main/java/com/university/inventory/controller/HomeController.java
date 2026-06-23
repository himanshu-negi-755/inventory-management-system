package com.university.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple home controller that lists all available API endpoints.
 * Shown when visiting http://localhost:8080 in the browser.
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("app", "Inventory Management System");
        info.put("status", "Running");
        info.put("version", "1.0.0");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("GET  /products",                  "List all products");
        endpoints.put("GET  /products/{id}",             "Get product by ID");
        endpoints.put("GET  /products/search?name=",     "Search products by name");
        endpoints.put("POST /products",                  "Create a new product");
        endpoints.put("PUT  /products/{id}",             "Update a product");
        endpoints.put("DELETE /products/{id}",           "Delete a product");
        endpoints.put("POST /stock/add",                 "Add stock to a product");
        endpoints.put("POST /stock/remove",              "Remove stock from a product");
        endpoints.put("GET  /stock/history",             "All stock history");
        endpoints.put("GET  /stock/history/{productId}", "Stock history for one product");
        endpoints.put("GET  /dashboard",                 "Inventory summary statistics");

        info.put("endpoints", endpoints);
        return info;
    }
}
