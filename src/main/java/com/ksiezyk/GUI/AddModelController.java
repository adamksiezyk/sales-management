package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ModelDao;
import com.ksiezyk.Daos.ProductDao;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class AddModelController {
    private int productId;
    public TextField nameField;
    public TextField quantityField;
    public TableView<ModelDao> modelsTable;

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @FXML
    public void addModel() {
        String modelName = nameField.getText().trim();
        String quantityString = quantityField.getText();
        // Check if not empty
        if (modelName.isEmpty() || quantityString.isEmpty()) {
            ErrorDialog.showErrorDialog("Błąd danych", "Błąd podczas dodawania modelu",
                    "Uzupełnij wymagane puste pola:\n- nazwa modelu\n- ilość");
            return;
        }
        int modelQuantity = Integer.parseInt(quantityString);
        ModelDao newModelDao = new ModelDao(this.productId, modelName, modelQuantity);
        try {
            newModelDao.addModel();
        }
        catch (SQLException e1) {
            ErrorDialog.showErrorDialog(e1, "add new model");
        }
        try {
            this.updateTable();
            this.nameField.requestFocus();
        }
        catch (SQLException e) {
            ErrorDialog.showErrorDialog(e, "add Model");
        }
    }

    @FXML
    public void deleteModel() {
        ObservableList<ModelDao> modelDaos = this.modelsTable.getSelectionModel().getSelectedItems();
        try {
            ModelDao.deleteModel(modelDaos);
            updateTable();
        }
        catch (SQLIntegrityConstraintViolationException e) {
            ErrorDialog.showErrorDialog("Błąd danych", "Nie można usunąć modelu",
                    "Nie można usunąć modelu, ponieważ istnieje zamówienie na ten model.");
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "deleting model");
        }
    }

    public void updateTable() throws SQLException{
        ObservableList<ModelDao> modelDaos = ModelDao.getModels(this.productId);
        this.modelsTable.setItems(modelDaos);
    }

    @FXML
    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.DELETE) {
            this.deleteModel();
        }
        else if (e.getCode() == KeyCode.F2) {
            this.editModel();
        }
        else if (e.getCode() == KeyCode.ESCAPE) {
            this.getStage().close();
        }
        else if (e.getCode() == KeyCode.ENTER) {
            this.addModel();
        }
    }

    private Stage getStage() {
        return (Stage) this.modelsTable.getScene().getWindow();
    }

    @FXML
    public void editModel() {
        try {
            Stage magazineStage = this.getStage();
            ModelDao selectedModelDao = this.getSelectedModel();
            new ModelEditor(magazineStage, selectedModelDao);
            updateTable();
        }
        catch (Exception e1) {
            ErrorDialog.showErrorDialog(e1, "edit Model");
        }
    }

    public ModelDao getSelectedModel() {
        return this.modelsTable.getSelectionModel().getSelectedItem();
    }
}
