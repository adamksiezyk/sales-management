package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ProductDao;
import com.ksiezyk.Daos.TransmissionDao;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoadTransmission {
    private final Stage loadTransmissionWindow;
    private final LoadTransmissionController controller;

    public LoadTransmission() throws Exception {

        // Init scene
        this.loadTransmissionWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loadTransmission.fxml"));
        Parent root = fxmlLoader.load();
        Scene loadTransmissionScene = new Scene(root, 800, 650);
        this.controller = fxmlLoader.getController();

        // Configure transmissions table
        TableView<TransmissionDao> transmissionTable = (TableView<TransmissionDao>)
                loadTransmissionScene.lookup("#transmissionsTable");
        transmissionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Add double click listener
        transmissionTable.setRowFactory( e -> {
            TableRow<TransmissionDao> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    this.controller.loadTransmissions();
                }
            });
            return row;
        });
        // Add columns
        TableColumn<TransmissionDao, Integer> transmissionId = new TableColumn<>("ID");
        transmissionId.setCellValueFactory(new PropertyValueFactory<>("id"));
        transmissionId.setPrefWidth(50);
        transmissionId.setSortType(TableColumn.SortType.DESCENDING);
        TableColumn<TransmissionDao, java.sql.Timestamp> transmissionDate = new TableColumn<>("Data");
        transmissionDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        transmissionDate.setPrefWidth(150);

        transmissionTable.getColumns().addAll(transmissionId, transmissionDate);

        // Add items
        this.controller.setIdColumn(transmissionId);
        this.controller.updateTable();

        loadTransmissionScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        this.loadTransmissionWindow.setTitle("Załaduj transmisję");
        this.loadTransmissionWindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        this.loadTransmissionWindow.setScene(loadTransmissionScene);
        this.loadTransmissionWindow.setMinHeight(650);
        this.loadTransmissionWindow.setMinWidth(800);
    }

    public ObservableList<TransmissionDao> showAndWait(Stage primaryStage) {
        this.loadTransmissionWindow.initModality(Modality.WINDOW_MODAL);
        this.loadTransmissionWindow.initOwner(primaryStage);
        this.loadTransmissionWindow.showAndWait();
        return this.controller.getSelectedTransmissions();
    }
}
