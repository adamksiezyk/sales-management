package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ModelDao;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ModelEditorController {
    public int selectedModelId = 0;
    public TextField selectedModelName;
    public TextField selectedModelQuantity;

    @FXML
    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            this.editSelectedModel();
        }
    }

    @FXML
    public void editSelectedModel() {
        String modelName = selectedModelName.getText();
        String modelQuantityString = selectedModelQuantity.getText().replace(',', '.');
        if (modelName.isEmpty() || modelQuantityString.isEmpty()) {
            ErrorDialog.showErrorDialog("Błąd danych", "Błąd podczas edytowania modelu",
                    "Uzupełnij wymagane puste pola:\n- nazwa modelu\n- ilość");
            return;
        }
        try {
            ModelDao.editModel(
                    selectedModelId,
                    modelName,
                    Integer.parseInt(modelQuantityString)
            );
            this.closeEditor();
        }
        catch (NumberFormatException e) {
            ErrorDialog.showErrorDialog("Błąd danych", "Błąd podczas edytowania modelu",
                    "Podano błędną ilość");
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "editing model");
        }
    }

    public void setId(int id) {
        this.selectedModelId = id;
    }

    public void closeEditor() {
        Stage editorStage = (Stage) this.selectedModelName.getScene().getWindow();
        editorStage.close();
    }
}
