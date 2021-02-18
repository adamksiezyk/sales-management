package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ClientDao;
import com.ksiezyk.Daos.DataDao;
import com.ksiezyk.Daos.TransmissionDao;
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

import java.io.IOException;

public class ShowClientProducts {
    public ShowClientProducts(Stage primaryStage, ClientDao clientDao, ObservableList<TransmissionDao> transmissionDaos) {
        try {
            // Init scene
            Stage clientWindow = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("showClientProducts.fxml"));
            Parent root = fxmlLoader.load();
            Scene clientScene = new Scene(root, 1020, 600);

            ShowClientProductsController controller = fxmlLoader.getController();
            controller.setClient(clientDao);
            controller.setTransmissions(transmissionDaos);

            // Configure table
            TableView<DataDao> clientsTable = (TableView) clientScene.lookup("#productsTable");
            // Add columns
            TableColumn<DataDao, String> nameColumn = new TableColumn<>("Nazwa");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
            nameColumn.setPrefWidth(300);
            TableColumn<DataDao, String> modelColumn = new TableColumn<>("Model");
            modelColumn.setCellValueFactory(new PropertyValueFactory<>("modelName"));
            modelColumn.setPrefWidth(300);
            TableColumn<DataDao, Integer> quantityColumn = new TableColumn<>("Ilość");
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            quantityColumn.setPrefWidth(100);
            TableColumn<DataDao, String> commentColumn = new TableColumn<>("Komentarz");
            commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
            commentColumn.setPrefWidth(300);
            clientsTable.getColumns().addAll(nameColumn, modelColumn, quantityColumn, commentColumn);
            controller.updateTable();

            clientScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            clientWindow.setTitle(clientDao.getName());
            clientWindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
            clientWindow.setScene(clientScene);
            clientWindow.setHeight(600);
            clientWindow.setWidth(1020);
            clientWindow.initModality(Modality.WINDOW_MODAL);
            clientWindow.initOwner(primaryStage);
            clientWindow.showAndWait();
        }
        catch (IOException e) {
            ErrorDialog.showErrorDialog(e, "view client products");
        }
    }
}
