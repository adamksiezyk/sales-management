package com.ksiezyk.GUI;

import com.ksiezyk.Daos.TransmissionDao;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoadTransmissionController {
    public TableView<TransmissionDao> transmissionsTable;
    public TableColumn<TransmissionDao, Integer> idColumn;

    public void setIdColumn(TableColumn<TransmissionDao, Integer> idColumn) {
        this.idColumn = idColumn;
    }

    public TransmissionDao getSelectedTransmission() {
        return this.transmissionsTable.getSelectionModel().getSelectedItem();
    }

    public ObservableList<TransmissionDao> getSelectedTransmissions() {
        return this.transmissionsTable.getSelectionModel().getSelectedItems();
    }

    @FXML
    public void searchTransmission() {

    }

    @FXML
    public void loadTransmissions() {
        this.getStage().close();
    }

    public Stage getStage() {
        return (Stage) this.transmissionsTable.getScene().getWindow();
    }

    @FXML
    public void deleteTransmission() {
        ObservableList<TransmissionDao> transmissionDaos = getSelectedTransmissions();
        int[] transmissionIds = transmissionDaos.stream().mapToInt(TransmissionDao::getId).toArray();
        try {
            TransmissionDao.deleteTransmissions(transmissionIds);
        } catch (SQLException e) {
            ErrorDialog.showErrorDialog(e, "delete transmissions");
        }
        finally {
            this.updateTable();
        }
    }

    public void updateTable() {
        try {
            this.transmissionsTable.setItems(TransmissionDao.getTransmissions());
            this.transmissionsTable.getSortOrder().setAll(this.idColumn);
        } catch (SQLException e) {
            ErrorDialog.showErrorDialog(e, "load transmissions");
        }
    }
}
