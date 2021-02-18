package com.ksiezyk.GUI;

import com.ksiezyk.Daos.TransmissionDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.Stage;


public class Controller {
    private Stage mainStage;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    void mail() {
        System.out.println("mail");
    }

    @FXML
    void openMagazine() {
        try {
            Magazine magazine = new Magazine();
            magazine.show();
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "open magazine");
        };
    }

    @FXML
    void newTransmission() {
        try {
            TransmissionDao transmissionDao = new TransmissionDao();
            transmissionDao.addToDatabase();
            new Transmission(FXCollections.observableArrayList(transmissionDao));
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "open new transmission");
        };
    }

    @FXML
    void loadTransmission() {
        try {
            LoadTransmission transmissionLoader = new LoadTransmission();
            new Transmission(transmissionLoader.showAndWait(this.mainStage));
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "load transmission");
        }
    }
}
