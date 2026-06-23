# Inventory Management System
**University Project — Spring Boot + MySQL REST API**

---

## Project Structure

```
src/main/java/com/university/inventory/
├── InventoryManagementApplication.java   ← App entry point
├── controller/
│   ├── ProductController.java            ← Product REST endpoints
│   ├── StockController.java              ← Stock REST endpoints
│   └── DashboardController.java          ← Dashboard endpoint
├── service/
│   ├── ProductService.java               ← Product business logic
│   ├── StockService.java                 ← Stock business logic
│   └── DashboardService.java             ← Dashboard aggregation
├── repository/
│   ├── ProductRepository.java            ← Product DB queries
│   └── StockHistoryRepository.java       ← StockHistory DB queries
├── model/
│   ├── Product.java                      ← Product entity
│   └── StockHistory.java                 ← StockHistory entity
├── dto/
│   ├── ProductRequest.java               ← Incoming product data
│   ├── StockRequest.java                 ← Incoming stock data
│   └── DashboardResponse.java            ← Dashboard response
└── exception/
    ├── ProductNotFoundException.java      ← Custom 404 exception
    ├── InsufficientStockException.java    ← Custom 400 exception
    └── GlobalExceptionHandler.java        ← @ControllerAdvice handler
```

---

## Prerequisites

1. **Java 17+** installed
2. **Maven 3.8+** installed
3. **MySQL 8+** running locally

---

## Database Setup

Run these commands in your MySQL client:

```sql
CREATE DATABASE inventory_db;
```

Then update `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_actual_password
```

---

## Running the Application

```bash
# From the project root folder
mvn spring-boot:run
```

The server starts at: `http://localhost:8081`

Tables are auto-created by Hibernate on first run (`ddl-auto=update`).

---

## API Reference

### Products

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/products` | Get all products |
| GET | `/products/{id}` | Get product by ID |
| GET | `/products/search?name=keyword` | Search by name |
| POST | `/products` | Create new product |
| PUT | `/products/{id}` | Update product |
| DELETE | `/products/{id}` | Delete product |

### Stock

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/stock/add` | Add stock to product |
| POST | `/stock/remove` | Remove stock from product |
| GET | `/stock/history` | All stock history |
| GET | `/stock/history/{productId}` | History for one product |

### Dashboard

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/dashboard` | Inventory summary stats |

---

## Sample Postman Requests

### 1. Create a Product
```
POST http://localhost:8081/products
Content-Type: application/json

{
  "name": "Laptop",
  "category": "Electronics",
  "price": 75000.00,
  "quantity": 10
}
```

### 2. Get All Products
```
GET http://localhost:8081/products
```

### 3. Search Products
```
GET http://localhost:8081/products/search?name=lap
```

### 4. Update a Product
```
PUT http://localhost:8081/products/1
Content-Type: application/json

{
  "name": "Laptop Pro",
  "category": "Electronics",
  "price": 85000.00,
  "quantity": 8
}
```

### 5. Delete a Product
```
DELETE http://localhost:8081/products/1
```

### 6. Add Stock
```
POST http://localhost:8081/stock/add
Content-Type: application/json

{
  "productId": 1,
  "quantity": 5
}
```

### 7. Remove Stock
```
POST http://localhost:8081/stock/remove
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

### 8. Get Stock History
```
GET http://localhost:8081/stock/history
```

### 9. Dashboard
```
GET http://localhost:8081/dashboard
```

---

## Sample Responses

### GET /dashboard
```json
{
  "totalProducts": 5,
  "totalCategories": 3,
  "lowStockCount": 2,
  "lowStockProducts": [
    { "id": 2, "name": "Mouse", "category": "Electronics", "price": 500.0, "quantity": 3 },
    { "id": 4, "name": "Notebook", "category": "Stationery", "price": 50.0, "quantity": 1 }
  ]
}
```

### Error Response (404)
```json
{
  "timestamp": "2024-06-22T14:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with ID: 99"
}
```

### Validation Error (400)
```json
{
  "timestamp": "2024-06-22T14:00:00",
  "status": 400,
  "errors": {
    "name": "Product name is required",
    "price": "Price must be zero or positive"
  }
}
```

---

## JavaFX Frontend (Bonus)

To connect a JavaFX desktop app to this backend:

1. **Add HTTP client dependency** (e.g., OkHttp or Java's `java.net.http.HttpClient`):
```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>
```

2. **Make HTTP calls from JavaFX controllers**:
```java
// Example: fetch all products
OkHttpClient client = new OkHttpClient();
Request request = new Request.Builder()
    .url("http://localhost:8081/products")
    .build();
try (Response response = client.newCall(request).execute()) {
    String json = response.body().string();
    // Parse JSON with Gson or Jackson and populate TableView
}
```

3. **Use TableView** to display products and bind ObservableList to parsed data.

4. **Run both** the Spring Boot backend and JavaFX app simultaneously — the JavaFX app consumes the REST API.
