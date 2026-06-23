package com.university.inventory.gui.view;

import com.university.inventory.gui.api.ApiClient;
import com.university.inventory.gui.model.Product;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Products tab: full CRUD interface.
 * - Table showing all products
 * - Form on the right to add or edit
 * - Search bar at the top
 * - Delete button
 */
public class ProductsTab {

    private final BorderPane content;
    private final TableView<Product> table = new TableView<>();
    private final ApiClient api;

    // Form fields
    private final TextField nameField     = new TextField();
    private final TextField categoryField = new TextField();
    private final TextField priceField    = new TextField();
    private final TextField quantityField = new TextField();

    // Currently selected product (null = add mode)
    private Product selectedProduct = null;

    public ProductsTab(ApiClient api) {
        this.api = api;

        // ── Search bar ────────────────────────────────────
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name…");
        searchField.setPrefWidth(260);

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("btn-secondary");

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("btn-secondary");

        searchBtn.setOnAction(e -> {
            String q = searchField.getText().trim();
            if (!q.isEmpty()) loadSearch(q);
            else loadAll();
        });
        clearBtn.setOnAction(e -> { searchField.clear(); loadAll(); });

        HBox searchBar = new HBox(8, new Label("🔍"), searchField, searchBtn, clearBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(0, 0, 10, 0));

        // ── Product table ─────────────────────────────────
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        table.setPlaceholder(new Label("No products found"));

        TableColumn<Product, Long>    idCol  = col("ID",       "id",       60);
        TableColumn<Product, String>  nameCol = col("Name",    "name",     180);
        TableColumn<Product, String>  catCol  = col("Category","category", 140);
        TableColumn<Product, Double>  priCol  = col("Price ₹", "price",    100);
        TableColumn<Product, Integer> qtyCol  = col("Qty",     "quantity", 70);
        table.getColumns().addAll(idCol, nameCol, catCol, priCol, qtyCol);

        // Clicking a row fills the form for editing
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) fillForm(sel);
        });

        // ── Delete button ─────────────────────────────────
        Button deleteBtn = new Button("🗑  Delete Selected");
        deleteBtn.getStyleClass().add("btn-danger");
        deleteBtn.setOnAction(e -> deleteSelected());

        Button refreshBtn = new Button("⟳  Refresh");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> loadAll());

        HBox tableToolbar = new HBox(8, refreshBtn, deleteBtn);
        tableToolbar.setAlignment(Pos.CENTER_RIGHT);
        tableToolbar.setPadding(new Insets(8, 0, 0, 0));

        VBox tableSection = new VBox(8, searchBar, table, tableToolbar);
        VBox.setVgrow(table, Priority.ALWAYS);

        // ── Form panel ────────────────────────────────────
        Label formTitle = new Label("Add / Edit Product");
        formTitle.getStyleClass().add("section-title");

        nameField.setPromptText("e.g. Laptop");
        categoryField.setPromptText("e.g. Electronics");
        priceField.setPromptText("e.g. 75000");
        quantityField.setPromptText("e.g. 10");

        Button saveBtn = new Button("💾  Save");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        Button clearFormBtn = new Button("✖  Clear Form");
        clearFormBtn.getStyleClass().add("btn-secondary");
        clearFormBtn.setMaxWidth(Double.MAX_VALUE);

        saveBtn.setOnAction(e -> saveProduct());
        clearFormBtn.setOnAction(e -> clearForm());

        VBox form = new VBox(10,
            formTitle,
            formLabel("Product Name"), nameField,
            formLabel("Category"),     categoryField,
            formLabel("Price (₹)"),    priceField,
            formLabel("Quantity"),     quantityField,
            new Separator(),
            saveBtn,
            clearFormBtn
        );
        form.setPadding(new Insets(16));
        form.setPrefWidth(260);
        form.getStyleClass().add("form-panel");

        // ── Root layout ───────────────────────────────────
        content = new BorderPane();
        content.setCenter(tableSection);
        content.setRight(form);
        content.setPadding(new Insets(20));
        BorderPane.setMargin(form, new Insets(0, 0, 0, 16));

        loadAll();
    }

    /** Loads all products from API into the table */
    private void loadAll() {
        new Thread(() -> {
            try {
                var list = api.getAllProducts();
                Platform.runLater(() -> table.getItems().setAll(list));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Load failed: " + e.getMessage()));
            }
        }).start();
    }

    private void loadSearch(String q) {
        new Thread(() -> {
            try {
                var list = api.searchProducts(q);
                Platform.runLater(() -> table.getItems().setAll(list));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Search failed: " + e.getMessage()));
            }
        }).start();
    }

    /** Fills the form with the selected product's data (edit mode) */
    private void fillForm(Product p) {
        selectedProduct = p;
        nameField.setText(p.getName());
        categoryField.setText(p.getCategory());
        priceField.setText(String.valueOf(p.getPrice()));
        quantityField.setText(String.valueOf(p.getQuantity()));
    }

    private void clearForm() {
        selectedProduct = null;
        nameField.clear();
        categoryField.clear();
        priceField.clear();
        quantityField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void saveProduct() {
        try {
            String name     = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double price    = Double.parseDouble(priceField.getText().trim());
            int    quantity = Integer.parseInt(quantityField.getText().trim());

            if (name.isEmpty() || category.isEmpty()) {
                showError("Name and Category are required.");
                return;
            }

            Product p = new Product(name, category, price, quantity);

            new Thread(() -> {
                try {
                    if (selectedProduct == null) {
                        api.createProduct(p);
                    } else {
                        p.setId(selectedProduct.getId());
                        api.updateProduct(p);
                    }
                    Platform.runLater(() -> { clearForm(); loadAll(); });
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Save failed: " + e.getMessage()));
                }
            }).start();

        } catch (NumberFormatException ex) {
            showError("Price and Quantity must be valid numbers.");
        }
    }

    private void deleteSelected() {
        Product sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Select a product to delete."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete \"" + sel.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirm Delete");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        api.deleteProduct(sel.getId());
                        Platform.runLater(() -> { clearForm(); loadAll(); });
                    } catch (Exception e) {
                        Platform.runLater(() -> showError("Delete failed: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    private Label formLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("form-label");
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

    public BorderPane getContent() { return content; }
}
