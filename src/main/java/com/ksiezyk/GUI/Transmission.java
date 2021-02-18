package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ClientDao;
import com.ksiezyk.Daos.ModelDao;
import com.ksiezyk.Daos.ProductDao;
import com.ksiezyk.Daos.TransmissionDao;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.stream.Collectors;

public class Transmission {
    public final ObservableList<TransmissionDao> transmissionDaos;

    public Transmission(ObservableList<TransmissionDao> transmissionDaos) throws Exception{
        this.transmissionDaos = transmissionDaos;
        if (transmissionDaos.isEmpty()) return;

        // Init scene
        Stage transmissionWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("transmission.fxml"));
        Parent root = fxmlLoader.load();
        Scene transmissionScene = new Scene(root, 600, 400);
        TransmissionController controller = fxmlLoader.getController();

        // Set transmission ID
        controller.setTransmissions(transmissionDaos);

        // Configure products table
        TableView<ProductDao> productsTable = (TableView) transmissionScene.lookup("#productsTable");
        // Allow for multiple selections
        productsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Add columns
        TableColumn<ProductDao, String> productsCodeColumn = new TableColumn<>("Kod");
        productsCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        productsCodeColumn.setPrefWidth(80);
        TableColumn<ProductDao, String> productsNameColumn = new TableColumn<>("Nazwa");
        productsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productsNameColumn.setPrefWidth(200);
        TableColumn<ProductDao, String> productsQuantityColumn = new TableColumn<>("Ilość");
        productsQuantityColumn.setCellValueFactory(row -> {
            try {
                ObservableList<ModelDao> modelDaos = ModelDao.getModels(row.getValue().getId());
                String options = modelDaos.stream()
                        .map(model -> model.getName() + ": " + model.getQuantity())
                        .collect(Collectors.joining("\n"));
                return new ReadOnlyStringWrapper(options);
            }
            catch (SQLException e) {
                ErrorDialog.showErrorDialog(e, "load models");
            }
            return null;
        });
        productsQuantityColumn.setPrefWidth(200);
        TableColumn<ProductDao, Double> productsPriceColumn = new TableColumn<>("Cena");
        productsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productsPriceColumn.setPrefWidth(70);
        // Format price to .2f
        productsPriceColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(NumberFormat.getCurrencyInstance().format(price));
                }
            }
        });
        productsTable.getColumns().addAll(productsCodeColumn, productsNameColumn, productsQuantityColumn,
                productsPriceColumn);
        // Row double click listener
        productsTable.setRowFactory( e -> {
            TableRow<ProductDao> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ProductDao rowData = row.getItem();
                    new AddClientToProduct(rowData, this.transmissionDaos);
                    controller.updateClientsTable();
                    productsTable.refresh();
                }
            });
            return row;
        });

        // Configure clients table
        TableView<ClientDao> clientsTable = (TableView) transmissionScene.lookup("#clientsTable");
        // Allow for multiple selections
        clientsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Add columns
        TableColumn<ClientDao, String> clientsNameColumn = new TableColumn<>("Nazwa");
        clientsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientsNameColumn.setPrefWidth(200);
        clientsTable.getColumns().addAll(clientsNameColumn);
        // Row double click listener
        clientsTable.setRowFactory( e -> {
            TableRow<ClientDao> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ClientDao rowData = row.getItem();
                    new ShowClientProducts(transmissionWindow,
                            rowData,
                            this.transmissionDaos);
                    controller.updateClientsTable();
                    productsTable.refresh();
                }
            });
            return row;
        });

        // Load orderDaos
        controller.loadTransmission();

        transmissionScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        transmissionWindow.setTitle("Transmisja");
        transmissionWindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        transmissionWindow.setScene(transmissionScene);
        transmissionWindow.setMinHeight(600);
        transmissionWindow.setMinWidth(800);
        transmissionWindow.setMaximized(true);
        transmissionWindow.show();
    }
}
