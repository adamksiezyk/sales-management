package com.ksiezyk.GUI;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


public class ErrorDialog {

    public static void showErrorDialog(Exception e, String action) {
        String exceptionName = e.getClass().getName();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(exceptionName + " error");
        alert.setHeaderText(String.format("A %s error occurred while trying to %s", exceptionName, action));
        try {
            FileOutputStream fos = new FileOutputStream(new File("log.txt"), true);
            PrintStream ps = new PrintStream(fos);
            ps.println(java.time.LocalDateTime.now());
            e.printStackTrace(ps);
            alert.setContentText("Error saved to log.txt");
        }
        catch (FileNotFoundException e1) {
            alert.setContentText("Error while saving the error to log.txt");
        }
        alert.showAndWait();
    }

    public static void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
