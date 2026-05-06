package com.example.dashboard.ui;

import com.example.dashboard.model.AlertEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.util.List;

public class MaliciousReportController {
    @FXML
    private ListView<String> alertList;
    @FXML
    private TextArea detailsArea;

    /**
     * Display a list of alerts in the simple UI list view.
     */
    public void showAlerts(List<AlertEntry> alerts) {
        Platform.runLater(() -> {
            alertList.getItems().clear();
            for (AlertEntry a : alerts) {
                String who = (a.getUser() != null && !a.getUser().isEmpty()) ? a.getUser() : a.getIp();
                alertList.getItems().add(String.format("%s - %s: %s", a.getType(), who, a.getDescription()));
            }
        });
    }

    /**
     * Show detailed information for a single alert.
     */
    public void showDetails(AlertEntry a) {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Timestamp: ").append(a.getTimestamp()).append("\n");
            sb.append("Type: ").append(a.getType()).append("\n");
            sb.append("Severity: ").append(a.getSeverity()).append("\n");
            sb.append("User: ").append(a.getUser()).append("\n");
            sb.append("IP: ").append(a.getIp()).append("\n\n");
            sb.append(a.getDescription());
            detailsArea.setText(sb.toString());
        });
    }
}
