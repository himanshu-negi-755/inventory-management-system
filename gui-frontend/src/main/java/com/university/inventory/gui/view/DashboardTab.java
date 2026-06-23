package com.university.inventory.gui.view;

import com.university.inventory.gui.api.ApiClient;
import com.university.inventory.gui.model.Dashboard;
import com.university.inventory.gui.model.Product;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Dashboard tab showing summary statistics and low-stock alerts.
 */
public class DashboardTab {

    private final VBox content;

    public DashboardTab(ApiClient api) {
        // ── Stat cards ───────────────────────────────────
        Label totalProductsVal  = statValue("–");
        Label totalCategoriesVal = statValue("–");
        Label lowStockVal       = statValue("–");

        HBox cards = new HBox(16,
            statCard("Total Products",   "📦", totalProductsVal,  "#4A90D9"),
            statCard("Categories",       "🏷",  totalCategoriesVal, "#27AE60"),
            statCard("Low Stock Alerts", "⚠️",  lowStockVal,       "#E74C3C")
        );
        cards.setPadding(new Insets(0, 0, 20, 0));

        // ── Refresh button ───────────────────────────────
        Button refreshBtn = new Button("⟳  Refresh");
        refreshBtn.getStyleClass().add("btn-primary");

        // ── Low stock table ──────────────────────────────
        Label lowStockTitle = new Label("Low Stock Products  (quantity < 5)");
        lowStockTitle.getStyleClass().add("section-title");

        TableView<Product> lowTable = new TableView<>();
        lowTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lowTable.getStyleClass().add("table-view");

        TableColumn<Product, Long>   idCol  = col("ID",       "id",       80);
        TableColumn<Product, String> nameCol = col("Name",    "name",     200);
        TableColumn<Product, String> catCol  = col("Category","category", 160);
        TableColumn<Product, Double> priceCol = col("Price ₹","price",    120);
        TableColumn<Product, Integer> qtyCol = col("Qty",     "quantity", 80);
        lowTable.getColumns().addAll(idCol, nameCol, catCol, priceCol, qtyCol);
        lowTable.setPrefHeight(240);

        // ── Load data ─────────────────────────────────────
        Runnable loadData = () -> {
            new Thread(() -> {
                try {
                    Dashboard d = api.getDashboard();
                    Platform.runLater(() -> {
                        totalProductsVal.setText(String.valueOf(d.getTotalProducts()));
                        totalCategoriesVal.setText(String.valueOf(d.getTotalCategories()));
                        lowStockVal.setText(String.valueOf(d.getLowStockCount()));
                        lowTable.getItems().setAll(d.getLowStockProducts());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Failed to load dashboard: " + e.getMessage()));
                }
            }).start();
        };

        refreshBtn.setOnAction(e -> loadData.run());
        loadData.run(); // load on open

        // ── Layout ────────────────────────────────────────
        HBox toolbar = new HBox(refreshBtn);
        toolbar.setAlignment(Pos.CENTER_RIGHT);
        toolbar.setPadding(new Insets(0, 0, 10, 0));

        content = new VBox(16, cards, toolbar, lowStockTitle, lowTable);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("tab-content");
    }

    /** Creates a single stat card widget */
    private VBox statCard(String label, String icon, Label valueLabel, String color) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");

        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("card-label");

        VBox card = new VBox(6, iconLabel, valueLabel, nameLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: " + color + "22; -fx-background-radius: 10; " +
                      "-fx-border-color: " + color + "; -fx-border-radius: 10; -fx-border-width: 1.5;");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Label statValue(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("card-value");
        return l;
    }

    private <S, T> TableColumn<S, T> col(String title, String prop, double width) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText("Error");
        alert.showAndWait();
    }

    public VBox getContent() { return content; }
}
