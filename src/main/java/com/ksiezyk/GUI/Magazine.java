package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ProductDao;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.NumberFormat;

public class Magazine {
    public Stage magazineWindow;
    public Button addProductToTransmission;
    public MagazineController controller;

    public Magazine() throws Exception{
        // Init scene
        this.magazineWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("magazine.fxml"));
        Parent root = fxmlLoader.load();
        MagazineController controller = fxmlLoader.getController();
        Scene magazineScene = new Scene(root, 600, 400);

        // Init controller
        this.controller = fxmlLoader.getController();

        // Init addProductToTransmission button
        this.addProductToTransmission = (Button) magazineScene.lookup("#addProductToTransmission");

        // Products table
        TableView<ProductDao> productsTable = (TableView) magazineScene.lookup("#productsTable");
        // Allow for multiple selections
        productsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Add columns
        TableColumn<ProductDao, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        nameColumn.setPrefWidth(400);
        TableColumn<ProductDao, String> codeColumn = new TableColumn<>("Kod");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeColumn.setPrefWidth(200);
        TableColumn<ProductDao, Double> priceColumn = new TableColumn<>("Cena");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setPrefWidth(200);
        // Format price to .2f
        priceColumn.setCellFactory(tc -> new TableCell<>() {
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
        productsTable.getColumns().addAll(codeColumn, nameColumn, priceColumn);

        // Add products
        controller.setItems();

        // Add double click listener
        productsTable.setRowFactory( e -> {
            TableRow<ProductDao> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ProductDao rowData = row.getItem();
                    new AddModel(this.magazineWindow, rowData);
                }
            });
            return row;
        });

        magazineScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        magazineWindow.setTitle("Magazyn");
        magazineWindow.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        magazineWindow.setScene(magazineScene);
        magazineWindow.setMinHeight(650);
        magazineWindow.setMinWidth(800);
        magazineWindow.setMaximized(true);
    }

    public void show() {
        this.magazineWindow.show();
    }

    public ObservableList<ProductDao> showAndWait(Stage primaryStage) {
        this.magazineWindow.initModality(Modality.WINDOW_MODAL);
        this.magazineWindow.initOwner(primaryStage);
        this.magazineWindow.showAndWait();
        return controller.getSelectedProducts();
    }

    public void transmissionMode() {
        this.addProductToTransmission.setVisible(true);
    }
}
