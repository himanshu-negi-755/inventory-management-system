package com.university.inventory.gui.view;

import com.university.inventory.gui.api.ApiClient;
import com.university.inventory.gui.model.Product;
import com.university.inventory.gui.model.StockHistory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Stock tab: add/remove stock and view history.
 */
public class StockTab {

    private final BorderPane content;
    private final ApiClient api;
    private final TableView<StockHistory> historyTable = new TableView<>();
    private final ComboBox<Product> productCombo = new ComboBox<>();

    public StockTab(ApiClient api) {
        this.api = api;

        // ── Product selector ──────────────────────────────
        productCombo.setPromptText("Select product…");
        productCombo.setPrefWidth(260);

        Button loadProductsBtn = new Button("⟳");
        loadProductsBtn.setOnAction(e -> loadProducts());
        loadProductsBtn.getStyleClass().add("btn-secondary");

        HBox productRow = new HBox(8, new Label("Product:"), productCombo, loadProductsBtn);
        productRow.setAlignment(Pos.CENTER_LEFT);

        // ── Quantity field ────────────────────────────────
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setPrefWidth(120);

        // ── Add / Remove buttons ──────────────────────────
        Button addBtn = new Button("➕  Add Stock");
        addBtn.getStyleClass().add("btn-success");

        Button removeBtn = new Button("➖  Remove Stock");
        removeBtn.getStyleClass().add("btn-danger");

        addBtn.setOnAction(e -> {
            Product p = productCombo.getValue();
            if (p == null) { showError("Select a product first."); return; }
            performStock(p.getId(), quantityField, true);
        });

        removeBtn.setOnAction(e -> {
            Product p = productCombo.getValue();
            if (p == null) { showError("Select a product first."); return; }
            performStock(p.getId(), quantityField, false);
        });

        HBox actionRow = new HBox(10, new Label("Quantity:"), quantityField, addBtn, removeBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        // ── Stock operation panel ─────────────────────────
        Label opTitle = new Label("Stock Operation");
        opTitle.getStyleClass().add("section-title");

        VBox opPanel = new VBox(14, opTitle, productRow, actionRow);
        opPanel.setPadding(new Insets(16));
        opPanel.getStyleClass().add("form-panel");

        // ── History table ─────────────────────────────────
        Label histTitle = new Label("Stock History");
        histTitle.getStyleClass().add("section-title");

        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.getStyleClass().add("table-view");
        historyTable.setPlaceholder(new Label("No history yet"));

        // Product name column (nested property via custom cell factory)
        TableColumn<StockHistory, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(c -> {
            Product p = c.getValue().getProduct();
            return new javafx.beans.property.SimpleStringProperty(p != null ? p.getName() : "—");
        });
        productCol.setPrefWidth(180);

        TableColumn<StockHistory, String>  opCol  = col("Operation", "operation", 100);
        TableColumn<StockHistory, Integer> qtyCol = col("Quantity",  "quantity",  90);
        TableColumn<StockHistory, String>  tsCol  = col("Timestamp", "timestamp", 220);

        // Color-code ADD green, REMOVE red
        opCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("ADD".equals(item)
                        ? "-fx-text-fill: #27AE60; -fx-font-weight: bold;"
                        : "-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                }
            }
        });

        historyTable.getColumns().addAll(productCol, opCol, qtyCol, tsCol);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        Button refreshHistBtn = new Button("⟳  Refresh History");
        refreshHistBtn.getStyleClass().add("btn-secondary");
        refreshHistBtn.setOnAction(e -> loadHistory());

        VBox histSection = new VBox(8, histTitle, historyTable, refreshHistBtn);

        // ── Root layout ───────────────────────────────────
        content = new BorderPane();
        content.setTop(opPanel);
        content.setCenter(histSection);
        content.setPadding(new Insets(20));
        BorderPane.setMargin(opPanel, new Insets(0, 0, 16, 0));

        loadProducts();
        loadHistory();
    }

    private void performStock(Long productId, TextField qtyField, boolean isAdd) {
        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) { showError("Quantity must be at least 1."); return; }

            new Thread(() -> {
                try {
                    if (isAdd) api.addStock(productId, qty);
                    else       api.removeStock(productId, qty);
                    Platform.runLater(() -> {
                        qtyField.clear();
                        loadHistory();
                        loadProducts(); // refresh to show updated quantities in combo
                        showInfo("Stock " + (isAdd ? "added" : "removed") + " successfully.");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showError(e.getMessage()));
                }
            }).start();

        } catch (NumberFormatException ex) {
            showError("Enter a valid whole number for quantity.");
        }
    }

    private void loadProducts() {
        new Thread(() -> {
            try {
                List<Product> products = api.getAllProducts();
                Platform.runLater(() -> {
                    Product current = productCombo.getValue();
                    productCombo.setItems(FXCollections.observableArrayList(products));
                    if (current != null) {
                        products.stream()
                            .filter(p -> p.getId().equals(current.getId()))
                            .findFirst()
                            .ifPresent(productCombo::setValue);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to load products: " + e.getMessage()));
            }
        }).start();
    }

    private void loadHistory() {
        new Thread(() -> {
            try {
                List<StockHistory> history = api.getAllHistory();
                Platform.runLater(() -> historyTable.getItems().setAll(history));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to load history: " + e.getMessage()));
            }
        }).start();
    }

    private <S, T> TableColumn<S, T> col(String title, String prop, double width) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error"); a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText("Success"); a.showAndWait();
    }

    public BorderPane getContent() { return content; }
}
