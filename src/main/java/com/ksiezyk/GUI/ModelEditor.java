package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ModelDao;
import com.ksiezyk.Daos.ProductDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModelEditor {

    public ModelEditor(Stage primaryStage, ModelDao modelDao) throws Exception {
        // Init scene
        Stage modelEditorWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("modelEditor.fxml"));
        Parent root = fxmlLoader.load();
        Scene modelEditorScene = new Scene(root);
        ModelEditorController modelEditorController = fxmlLoader.getController();

        // Selected product details
        TextField selectedModelName = (TextField) modelEditorScene.lookup("#selectedModelName");
        TextField selectedModelQuantity = (TextField) modelEditorScene.lookup("#selectedModelQuantity");

        selectedModelName.setText(modelDao.getName());
        selectedModelQuantity.setText(Integer.toString(modelDao.getQuantity()));
        modelEditorController.setId(modelDao.getId());

        // Stage settings
        modelEditorScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        modelEditorWindow.setTitle("Edytuj Model");
        modelEditorWindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        modelEditorWindow.setScene(modelEditorScene);
        modelEditorWindow.initModality(Modality.WINDOW_MODAL);
        modelEditorWindow.initOwner(primaryStage);
        modelEditorWindow.setResizable(false);
        modelEditorWindow.showAndWait();
    }
}

