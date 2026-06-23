package com.university.inventory.gui.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.inventory.gui.model.Dashboard;
import com.university.inventory.gui.model.Product;
import com.university.inventory.gui.model.StockHistory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * HTTP client that communicates with the Spring Boot REST API.
 *
 * Uses Java's built-in java.net.http.HttpClient (no extra dependencies).
 * Jackson ObjectMapper parses JSON responses into model objects.
 *
 * All methods are synchronous for simplicity.
 * In production, you'd use async calls to avoid blocking the UI thread.
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8081";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // ─────────────────────────────────────────────
    // PRODUCT ENDPOINTS
    // ─────────────────────────────────────────────

    /** GET /products — returns all products */
    public List<Product> getAllProducts() throws Exception {
        String json = get("/products");
        return mapper.readValue(json, new TypeReference<List<Product>>() {});
    }

    /** GET /products/search?name=keyword */
    public List<Product> searchProducts(String name) throws Exception {
        String json = get("/products/search?name=" + name);
        return mapper.readValue(json, new TypeReference<List<Product>>() {});
    }

    /** POST /products — creates a new product */
    public Product createProduct(Product p) throws Exception {
        String body = mapper.writeValueAsString(Map.of(
            "name",     p.getName(),
            "category", p.getCategory(),
            "price",    p.getPrice(),
            "quantity", p.getQuantity()
        ));
        String json = post("/products", body);
        return mapper.readValue(json, Product.class);
    }

    /** PUT /products/{id} — updates an existing product */
    public Product updateProduct(Product p) throws Exception {
        String body = mapper.writeValueAsString(Map.of(
            "name",     p.getName(),
            "category", p.getCategory(),
            "price",    p.getPrice(),
            "quantity", p.getQuantity()
        ));
        String json = put("/products/" + p.getId(), body);
        return mapper.readValue(json, Product.class);
    }

    /** DELETE /products/{id} */
    public void deleteProduct(Long id) throws Exception {
        delete("/products/" + id);
    }

    // ─────────────────────────────────────────────
    // STOCK ENDPOINTS
    // ─────────────────────────────────────────────

    /** POST /stock/add */
    public StockHistory addStock(Long productId, int quantity) throws Exception {
        String body = mapper.writeValueAsString(Map.of("productId", productId, "quantity", quantity));
        String json = post("/stock/add", body);
        return mapper.readValue(json, StockHistory.class);
    }

    /** POST /stock/remove */
    public StockHistory removeStock(Long productId, int quantity) throws Exception {
        String body = mapper.writeValueAsString(Map.of("productId", productId, "quantity", quantity));
        String json = post("/stock/remove", body);
        return mapper.readValue(json, StockHistory.class);
    }

    /** GET /stock/history */
    public List<StockHistory> getAllHistory() throws Exception {
        String json = get("/stock/history");
        return mapper.readValue(json, new TypeReference<List<StockHistory>>() {});
    }

    // ─────────────────────────────────────────────
    // DASHBOARD
    // ─────────────────────────────────────────────

    /** GET /dashboard */
    public Dashboard getDashboard() throws Exception {
        String json = get("/dashboard");
        return mapper.readValue(json, Dashboard.class);
    }

    // ─────────────────────────────────────────────
    // PRIVATE HTTP HELPERS
    // ─────────────────────────────────────────────

    private String get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        checkStatus(response);
        return response.body();
    }

    private String post(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        checkStatus(response);
        return response.body();
    }

    private String put(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        checkStatus(response);
        return response.body();
    }

    private void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        checkStatus(response);
    }

    /** Throws a descriptive exception if the HTTP status indicates an error */
    private void checkStatus(HttpResponse<String> response) throws Exception {
        int status = response.statusCode();
        if (status >= 400) {
            // Try to extract "message" field from JSON error response
            try {
                Map<?, ?> err = mapper.readValue(response.body(), Map.class);
                Object msg = err.get("message");
                throw new Exception(msg != null ? msg.toString() : "HTTP " + status);
            } catch (Exception e) {
                if (e.getMessage() != null && !e.getMessage().startsWith("HTTP")) throw e;
                throw new Exception("HTTP Error " + status + ": " + response.body());
            }
        }
    }
}
