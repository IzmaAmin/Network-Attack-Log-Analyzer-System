package com.example.dashboard;

import com.example.dashboard.controller.MainController;
import com.example.dashboard.model.AlertEntry;
import com.example.dashboard.model.LogEntry;
import com.example.dashboard.model.Report;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;

public class MainApp extends Application {
    private final MainController controller = new MainController();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Attack Log Analyzer - Admin Dashboard");

        // Top buttons
        Button loadBtn = new Button("Load Logs");
        Button analyzeBtn = new Button("Analyze Activity");
        Button viewAlertsBtn = new Button("View Alerts");
        Button reportBtn = new Button("Generate Report");

        HBox topBar = new HBox(10, loadBtn, analyzeBtn, viewAlertsBtn, reportBtn);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Center: Log table
        TableView<LogEntry> table = new TableView<>();
        TableColumn<LogEntry, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(cell.getValue()::getTimestampString));
        TableColumn<LogEntry, String> appCol = new TableColumn<>("App");
        appCol.setCellValueFactory(new PropertyValueFactory<>("sourceApp"));
        TableColumn<LogEntry, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        TableColumn<LogEntry, String> ipCol = new TableColumn<>("IP");
        ipCol.setCellValueFactory(new PropertyValueFactory<>("ip"));
        TableColumn<LogEntry, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        TableColumn<LogEntry, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<LogEntry, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));

        table.getColumns().addAll(timeCol, appCol, userCol, ipCol, actionCol, statusCol, detailsCol);
        table.setItems(controller.getLogs());

        // Right pane: Alerts and Blacklist
        ListView<AlertEntry> alertView = new ListView<>(controller.getAlerts());
        alertView.setPrefHeight(300);
        VBox alertBox = new VBox(new Label("Alerts"), alertView);
        alertBox.setPadding(new Insets(10));

        ListView<String> blacklistView = new ListView<>(controller.getBlacklist());
        blacklistView.setPrefHeight(200);
        VBox blackBox = new VBox(new Label("Blacklist"), blacklistView);
        blackBox.setPadding(new Insets(10));

        VBox rightPane = new VBox(10, alertBox, blackBox);
        rightPane.setPadding(new Insets(10));
        rightPane.setPrefWidth(350);

        // Bottom: report area
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(4);
        VBox bottomBox = new VBox(new Label("Report"), reportArea);
        bottomBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(table);
        root.setRight(rightPane);
        root.setBottom(bottomBox);

        // Wire buttons
        loadBtn.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Select Logs Directory (or cancel to use default)");
            File dir = dc.showDialog(primaryStage);
            if (dir != null) {
                controller.setLogsDir(dir.toPath());
            }
            try {
                controller.loadLogs();
            } catch (Exception ex) {
                showError("Load error", ex.getMessage());
            }
        });

        analyzeBtn.setOnAction(e -> {
            controller.analyze();
            // update UI components
            reportArea.setText(controller.getLastReport() != null ? controller.getLastReport().toString() : "No report");
        });

        viewAlertsBtn.setOnAction(e -> {
            Alert dlg = new Alert(Alert.AlertType.INFORMATION);
            dlg.setTitle("Alerts");
            StringBuilder sb = new StringBuilder();
            for (AlertEntry a : controller.getAlerts()) sb.append(a.toString()).append("\n");
            dlg.getDialogPane().setContent(new ScrollPane(new TextArea(sb.toString())));
            dlg.showAndWait();
        });

        reportBtn.setOnAction(e -> {
            Report r = controller.generateReport();
            if (r == null) showInfo("Report", "No report available. Run analysis first.");
            else showInfo("Report", r.toString());
        });

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        // auto-load default logs if present
        try { controller.loadLogs(); } catch (Exception ex) {/* ignore */}
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
