package com.ksiezyk.GUI;

import com.ksiezyk.Daos.DataDao;
import com.ksiezyk.Daos.ModelDao;
import com.ksiezyk.Daos.ProductDao;
import com.ksiezyk.Daos.TransmissionDao;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class AddClientToProduct {
    public AddClientToProduct(ProductDao productDao, ObservableList<TransmissionDao> transmissionDaos) {
        try {
            // Init scene
            Stage addClientWindow = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addClientToProduct.fxml"));
            Parent root = fxmlLoader.load();
            Scene addClientScene = new Scene(root, 600, 400);

            // Set transmission ID
            AddClientToProductController controller = fxmlLoader.getController();
            controller.setTransmissions(transmissionDaos);
            controller.setProduct(productDao);

            // Configure table
            TableView<DataDao> clientsTable = (TableView) addClientScene.lookup("#clientsTable");
            // Add columns
            TableColumn<DataDao, String> nameColumn = new TableColumn<>("Nazwa");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
            nameColumn.setPrefWidth(300);
            TableColumn<DataDao, String> modelColumn = new TableColumn<>("Model");
            modelColumn.setCellValueFactory(new PropertyValueFactory<>("modelName"));
            modelColumn.setPrefWidth(300);
            TableColumn<DataDao, Integer> quantityColumn = new TableColumn<>("Ilość");
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            quantityColumn.setPrefWidth(100);
            clientsTable.getColumns().addAll(nameColumn, modelColumn, quantityColumn);
            controller.updateTable();

            // Set model selection
            controller.setModels();

            addClientScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            addClientWindow.setTitle(productDao.getName());
            addClientWindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
            addClientWindow.setScene(addClientScene);
            addClientWindow.setHeight(650);
            addClientWindow.setWidth(800);
            addClientWindow.showAndWait();
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "add client to product");
        }
    }
}
