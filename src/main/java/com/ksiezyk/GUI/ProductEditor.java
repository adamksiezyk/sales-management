package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ProductDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductEditor {

    public ProductEditor(Stage primaryStage, ProductDao selectedProductDao) throws Exception {
        // Init scene
        Stage productEditorWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("productEditor.fxml"));
        Parent root = fxmlLoader.load();
        Scene productEditorScene = new Scene(root);
        ProductEditorController productEditorController = fxmlLoader.getController();

        // Selected product details
        TextField selectedProductName = (TextField) productEditorScene.lookup("#selectedProductName");
        TextField selectedProductCode = (TextField) productEditorScene.lookup("#selectedProductCode");
        TextField selectedProductPrice = (TextField) productEditorScene.lookup("#selectedProductPrice");

        selectedProductName.setText(selectedProductDao.getName());
        selectedProductCode.setText(selectedProductDao.getCode());
        selectedProductPrice.setText(String.format("%.2f", selectedProductDao.getPrice()));
        productEditorController.setId(selectedProductDao.getId());

        // Stage settings
        productEditorScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        productEditorWindow.setTitle("Edytuj produkt");
        productEditorWindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        productEditorWindow.setScene(productEditorScene);
        productEditorWindow.initModality(Modality.WINDOW_MODAL);
        productEditorWindow.initOwner(primaryStage);
        productEditorWindow.setResizable(false);
        productEditorWindow.showAndWait();
    }
}
