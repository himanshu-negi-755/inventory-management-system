package com.university.inventory.gui.view;

import com.university.inventory.gui.api.ApiClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Root layout of the application.
 * Contains a top header bar and a TabPane with three tabs:
 *  - Dashboard
 *  - Products
 *  - Stock
 */
public class MainView {

    private final BorderPane root;

    public MainView() {
        ApiClient api = new ApiClient();

        // ── Header bar ──────────────────────────────────
        Label title = new Label("📦  Inventory Management System");
        title.getStyleClass().add("header-title");

        Label subtitle = new Label("University Project  •  Spring Boot + JavaFX");
        subtitle.getStyleClass().add("header-subtitle");

        VBox titleBox = new VBox(2, title, subtitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label statusDot = new Label("● Connected");
        statusDot.getStyleClass().add("status-connected");

        HBox header = new HBox(titleBox, statusDot);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header-bar");
        header.setPadding(new Insets(14, 20, 14, 20));

        // ── Tabs ─────────────────────────────────────────
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("main-tabs");

        Tab dashTab     = new Tab("  Dashboard  ",   new DashboardTab(api).getContent());
        Tab productsTab = new Tab("  Products   ",   new ProductsTab(api).getContent());
        Tab stockTab    = new Tab("  Stock      ",   new StockTab(api).getContent());

        tabPane.getTabs().addAll(dashTab, productsTab, stockTab);

        // ── Root layout ───────────────────────────────────
        root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabPane);
        root.getStyleClass().add("root-pane");
    }

    public BorderPane getRoot() { return root; }
}
