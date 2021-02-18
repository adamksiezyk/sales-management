package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ModelDao;
import com.ksiezyk.Daos.ProductDao;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddModel {
    public AddModel(Stage primaryStage, ProductDao productDao) {
        try {
            // Init scene
            Stage addModelWindow = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addModel.fxml"));
            Parent root = fxmlLoader.load();
            Scene addModelScene = new Scene(root, 800, 400);
            AddModelController controller = fxmlLoader.getController();

            // Set product id
            controller.setProductId(productDao.getId());

            // Configure table
            TableView<ModelDao> modelsTable = (TableView) addModelScene.lookup("#modelsTable");
            // Add columns
            TableColumn<ModelDao, String> nameColumn = new TableColumn<>("Nazwa");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameColumn.setPrefWidth(400);
            TableColumn<ModelDao, String> quantityColumn = new TableColumn<>("Ilość");
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            quantityColumn.setPrefWidth(100);
            modelsTable.getColumns().addAll(nameColumn, quantityColumn);

            // Add items
            ObservableList<ModelDao> modelDaos = ModelDao.getModels(productDao.getId());
            modelsTable.setItems(modelDaos);

            addModelScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            addModelWindow.setTitle(productDao.getName());
            addModelWindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
            addModelWindow.setScene(addModelScene);
            addModelWindow.setHeight(400);
            addModelWindow.setWidth(800);
            addModelWindow.initModality(Modality.WINDOW_MODAL);
            addModelWindow.initOwner(primaryStage);
            addModelWindow.showAndWait();
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "add model");
        }
    }
}
