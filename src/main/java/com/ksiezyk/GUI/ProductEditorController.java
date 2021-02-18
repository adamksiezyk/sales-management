package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ProductDao;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ProductEditorController {
    public int selectedProductId = 0;
    public TextField selectedProductName;
    public TextField selectedProductCode;
    public TextField selectedProductPrice;

    @FXML
    public void keyPressed(KeyEvent e) throws SQLException {
        if (e.getCode() == KeyCode.ENTER) {
            this.editSelectedProduct();
        }
    }

    @FXML
    public void editSelectedProduct() throws SQLException {
        ProductDao.editProduct(
                selectedProductId,
                selectedProductName.getText(),
                selectedProductCode.getText(),
                Double.parseDouble(selectedProductPrice.getText().replace(',', '.'))
        );
        this.closeEditor();
    }

    public void setId(int id) {
        this.selectedProductId = id;
    }

    public void closeEditor() {
        Stage editorStage = (Stage) this.selectedProductName.getScene().getWindow();
        editorStage.close();
    }
}
