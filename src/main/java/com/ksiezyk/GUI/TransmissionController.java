package com.ksiezyk.GUI;

import com.ksiezyk.Daos.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class TransmissionController {
    public ObservableList<TransmissionDao> transmissionDaos;
    public TableView<ProductDao> productsTable;
    public ObservableList<ProductDao> productDaos;
    public FilteredList<ProductDao> filteredProductDaos;
    public ObservableList<ClientDao> clientDaos;
    public FilteredList<ClientDao> filteredClientDaos;
    public TableView<ClientDao> clientsTable;
    public TextField searchProductsField;
    public TextField searchClientsField;
    public TextField productName;
    public TextField productPrice;

    public void setTransmissions(ObservableList<TransmissionDao> transmissionDaos) {
        this.transmissionDaos = transmissionDaos;
    }

    public void loadTransmission() {
        // Load transmission
        // this.transmission has to be set
//        if (this.transmissionDaos == null) {
//            this.productDaos = FXCollections.observableArrayList();
//        }
//        else {
//            try {
//                ObservableList<OrderDao> orderDaos = FXCollections.observableArrayList();
//                for (TransmissionDao transmissionDao : this.transmissionDaos) {
//                    orderDaos.addAll(OrderDao.getOrders(transmissionDao.getId()));
//                }
//                this.productDaos = FXCollections.observableArrayList(
//                        orderDaos.stream().map(order -> {
//                            try {
//                                return ProductDao.getProduct(order.getProductId());
//                            } catch (SQLException e1) {
//                                ErrorDialog.showErrorDialog(e1, "add product");
//                                return null;
//                            }
//                        })
//                        .distinct()
//                        .toArray(ProductDao[]::new));
//            }
//            catch (Exception e1) {
//                ErrorDialog.showErrorDialog(e1, "add product");
//            }
//        }
        this.productDaos = FXCollections.observableArrayList();
        this.updateProductsTable();
        this.clientDaos = FXCollections.observableArrayList();
        this.updateClientsTable();
        this.filteredProductDaos = new FilteredList<>(this.productDaos);
        this.filteredClientDaos = new FilteredList<>(this.clientDaos);
        // Set both tables items
        this.productsTable.setItems(this.filteredProductDaos);
        this.clientsTable.setItems(this.filteredClientDaos);
        // Add both tables filter
        this.searchProductsField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredProductDaos.setPredicate(createProductPredicate(newValue))
        );
        this.searchClientsField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredClientDaos.setPredicate(createClientPredicate(newValue))
        );
    }

    @FXML
    public void addProduct() {
        String name = this.productName.getText().trim();
        String priceString = this.productPrice.getText().trim().replace(',', '.');
        // Check if not empty
        if (priceString.isEmpty() || name.isEmpty()) {
            ErrorDialog.showErrorDialog("Błąd danych", "Błąd podczas dodawania produktu",
                    "Uzupełnij wymagane puste pola:\n- nazwa lub kod produktu\n- cena");
            return;
        }
        double price  = Double.parseDouble(priceString);
        ProductDao productDao = new ProductDao(name, "", price, 1, 0);
        try {
            ProductDao.addProduct(productDao);
            this.updateProductsTable();
        }
        catch (Exception e1) {
            ErrorDialog.showErrorDialog(e1, "add product");
        }
        finally {
            this.productName.setText("");
            this.productPrice.setText("");
        }
    }

    @FXML
    public void fieldKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            this.addProduct();
        }
    }

    public Stage getStage() {
        return (Stage) this.productsTable.getScene().getWindow();
    }

    public void updateClientsTable() {
        try {
            ObservableList<OrderDao> orderDaos = FXCollections.observableArrayList();
            for (TransmissionDao transmissionDao : this.transmissionDaos) {
                orderDaos.addAll(OrderDao.getOrders(transmissionDao.getId()));
            }
            int[] clientIds = orderDaos.stream().mapToInt(OrderDao::getClientId).toArray();
            // Add clients
            for (int clientId : clientIds) {
                if (this.clientDaos.stream().noneMatch(client -> client.getId() == clientId)) {
                    ClientDao clientDao = ClientDao.getClient(clientId);
                    this.clientDaos.add(clientDao);
                }
            }
            // Remove clients
            this.clientDaos.removeIf(client -> IntStream.of(clientIds).noneMatch(
                    id -> id == client.getId()));
        }
        catch (SQLException e) {
            ErrorDialog.showErrorDialog(e, "update clients table");
        }
    }

    @FXML
    public void productsTableKeyPressed(KeyEvent e) {
//        if (e.getCode() == KeyCode.DELETE) {
//            this.deleteProduct();
//        }
        if (e.getCode() == KeyCode.F5) {
            this.updateProductsTable();
            this.productsTable.refresh();
        }
    }

    @FXML
    public void clientsTableKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.DELETE) {
            this.deleteClient();
        }
        else if (e.getCode() == KeyCode.F5) {
            this.updateClientsTable();
        }
    }

//    @FXML
//    public void deleteProduct() {
//        try {
//            // User confirms
//            ConfirmDialog confirm = new ConfirmDialog()
//                    .setTitle("Usuń produkty")
//                    .setHeader("Usunąć zaznaczone produkty?");
//            if (confirm.showDialog()) {
//                int[] productsIds = this.getSelectedProducts().stream().mapToInt(ProductDao::getId).toArray();
//                for (TransmissionDao transmissionDao : this.transmissionDaos) {
//                    OrderDao.deleteOrdersByProductId(transmissionDao.getId(), productsIds);
//                }
//                this.productDaos.removeAll(this.getSelectedProducts());
//                this.updateClientsTable();
//            }
//        }
//        catch (Exception e) {
//            ErrorDialog.showErrorDialog(e, "delete product");
//        }
//    }

//    @FXML
//    public void editProduct() {
//        try {
//            Stage magazineStage = this.getStage();
//            ProductDao selectedProductDao = this.getSelectedProduct();
//            new ProductEditor(magazineStage, selectedProductDao);
//            this.updateClientsTable();
//        }
//        catch (Exception e1) {
//            ErrorDialog.showErrorDialog(e1, "edit product");
//        }
//    }

    @FXML
    public void deleteClient() {
        try {
            // User confirms
            ConfirmDialog confirm = new ConfirmDialog()
                    .setTitle("Usuń klientów")
                    .setHeader("Usunąć zaznaczonych klientów?");
            if (confirm.showDialog()) {
                int[] clientsIds = this.getSelectedClients().stream().mapToInt(ClientDao::getId).toArray();
                for (TransmissionDao transmissionDao : this.transmissionDaos) {
                    OrderDao.deleteOrdersByClientId(transmissionDao.getId(), clientsIds);
                }
                this.updateClientsTable();
                this.productsTable.refresh();
            }
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "delete client");
        }
    }

//    public ProductDao getSelectedProduct() {
//        return this.productsTable.getSelectionModel().getSelectedItem();
//    }
//
//    public ObservableList<ProductDao> getSelectedProducts() {
//        return productsTable.getSelectionModel().getSelectedItems();
//    }

    public ClientDao getSelectedClient() {
        return this.clientsTable.getSelectionModel().getSelectedItem();
    }

    public ObservableList<ClientDao> getSelectedClients() {
        return clientsTable.getSelectionModel().getSelectedItems();
    }

    @FXML
    public void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz do pliku");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Pliki tekstowe", "*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());
        try {
            if (file == null) return;
            // Create new file, if already exists we use default dialog
            file.createNewFile();
            PrintWriter printWriter = new PrintWriter(file);

            // Select clients
            ObservableList<ClientDao> clientDaos = this.clientDaos;
            FXCollections.sort(clientDaos, Comparator.comparing(ClientDao::getName));
            for (ClientDao clientDao : clientDaos) {
                double sum = 0.0;

                printWriter.println(clientDao.getName());

                // Select clients data
                ObservableList<DataDao> clientsData = FXCollections.observableArrayList();
                for (TransmissionDao transmissionDao : this.transmissionDaos) {
                    clientsData.addAll(
                            DataDao.getClientData(
                                    transmissionDao.getId(),
                                    clientDao.getId()));
                }

                // Print orderDaos
                for (DataDao dataDao : clientsData) {
                    sum += dataDao.getPrice() * dataDao.getQuantity();

                    printWriter.println(String.format("\t%s %s: %.2f zł x %d",
                            dataDao.getProductName(),
                            dataDao.getModelName(),
                            dataDao.getPrice(),
                            dataDao.getQuantity())
                    );
                    // Print comment
                    if (!dataDao.getComment().isEmpty()) {
                        printWriter.println('\t' + dataDao.getComment());
                    }
                }

                // Shipping cost
                sum += 15.0;
                printWriter.println("\tKoszt wysyłki: 15zł");
                // Sum
                printWriter.println(String.format("\tSuma: %.2f zł", sum));
                // Clients separator
                printWriter.println(
                        '\n' + "---------------------------------------------------------------------------" + '\n');
            }

            printWriter.close();
        } catch (IOException | SQLException e) {
            ErrorDialog.showErrorDialog(e, "saving to file");
        }
    }

    private Predicate<ProductDao> createProductPredicate(String searchText){
        return product -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return ProductDao.findProduct(product, searchText);
        };
    }

    private Predicate<ClientDao> createClientPredicate(String searchText){
        return client -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return ClientDao.findClient(client, searchText);
        };
    }

    public void updateProductsTable() {
        try {
            ObservableList<ProductDao> productDaos = ProductDao.getProducts();
            int[] productIds = productDaos.stream().mapToInt(ProductDao::getId).toArray();
            // Add products
            for (ProductDao productDao : productDaos) {
                if (this.productDaos.stream().noneMatch(product -> product.getId() == productDao.getId())) {
                    this.productDaos.add(productDao);
                }
            }
            // Remove products
            this.productDaos.removeIf(product -> IntStream.of(productIds).noneMatch(
                    id -> id == product.getId()));
            // Update rest of products
        }
        catch (SQLException e) {
            ErrorDialog.showErrorDialog(e, "update products table");
        }
    }
}
