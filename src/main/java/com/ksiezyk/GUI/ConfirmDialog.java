package com.ksiezyk.GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmDialog {
    private String title = "";
    private String header = "";
    private String content = "";

    public ConfirmDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ConfirmDialog setHeader(String header) {
        this.header = header;
        return this;
    }

    public ConfirmDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean showDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(this.title);
        alert.setHeaderText(this.header);
        alert.setContentText(this.content);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){ return true; }
        return false;
    }
}
