package com.ksiezyk.GUI;

import com.ksiezyk.Daos.*;
import com.ksiezyk.Daos.TransmissionDao;
import com.ksiezyk.DatabaseConnector.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.SQLException;
import java.util.Arrays;

public class AddClientToProductController {
    public ObservableList<TransmissionDao> transmissionDaos;
    public ProductDao productDao;
    public TextField clientName;
    public TextField clientQuantity;
    public ListView<ModelDao> modelSelection;
    public TextField clientComment;
    public TableView<DataDao> clientsTable;

    public void setTransmissions(ObservableList<TransmissionDao> transmissionDaos) {
        this.transmissionDaos = transmissionDaos;
    }

    public void setProduct(ProductDao productDao) {
        this.productDao = productDao;
    }

    @FXML
    public void addClient() {
        String name = this.clientName.getText().trim();
        String quantityText = this.clientQuantity.getText().trim();
        String comment = this.clientComment.getText().trim();
        ClientDao clientDao = new ClientDao(name);
        ModelDao modelDao = modelSelection.getSelectionModel().getSelectedItem();
        // Check if any field is empty
        if (name.isEmpty() || quantityText.isEmpty() || quantityText.equals("0") || modelDao == null) {
            ErrorDialog.showErrorDialog("Błąd danych", "Puste pola",
                    "Proszę wypełnić wszystkie obowiązkowe pola:\n- imię i nazwisko\n- model\n- ilość");
            return;
        }
        try {
            // Select client from database
            int clientId = DatabaseConnector.checkIfExists(ClientDao.getTable(), "name", clientDao.getName());
            // If clientId == 0 => client doesn't exist
            if (clientId == 0) {
                clientDao.addToDatabase();
            }
            else {
                clientDao.setId(clientId);
            }
            // Construct new order
            int quantity = Integer.parseInt(quantityText);
            OrderDao orderDao = new OrderDao(
                    this.transmissionDaos.get(this.transmissionDaos.size()-1).getId(),
                    this.productDao.getId(),
                    modelDao.getId(),
                    clientDao.getId(),
                    quantity,
                    comment
            );
            orderDao.addToDatabase();
            this.updateTable();
            this.setModels();
            clientName.requestFocus();
        }
        catch (NumberFormatException e) {
            ErrorDialog.showErrorDialog("Błąd danych", "Błędna ilość",
                    "Wprowadzono błędną wartość w polu \"ilość\"");
        }
        catch (ArithmeticException e) {
            ErrorDialog.showErrorDialog("Błąd danych", "Błędna ilość",
                    "Wybrany produkt nie jest dostępny w takiej ilości");
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "add client");
        }
    }

    public void setModels() {
        try {
            ObservableList<ModelDao> modelDaos = ModelDao.getModels(productDao.getId());
            this.modelSelection.setItems(modelDaos);
        }
        catch (SQLException e) {
            ErrorDialog.showErrorDialog(e, "load models");
        }
    }

    public void updateTable() throws SQLException {
        ObservableList<OrderDao> orderDaos = FXCollections.observableArrayList();
        for (TransmissionDao transmissionDao : this.transmissionDaos) {
            orderDaos.addAll(OrderDao.getOrders(transmissionDao.getId()));
        }
        ObservableList<DataDao> clients = FXCollections.observableArrayList();
        for (OrderDao orderDao : orderDaos) {
            if (orderDao.getProductId() == this.productDao.getId()) {
                ClientDao clientDao = ClientDao.getClient(orderDao.getClientId());
                clients.add(new DataDao(
                        orderDao.getId(),
                        clientDao.getId(),
                        clientDao.getName(),
                        this.productDao.getId(),
                        this.productDao.getName(),
                        orderDao.getModelId(),
                        ModelDao.getModel(orderDao.getModelId()).getName(),
                        this.productDao.getPrice(),
                        orderDao.getQuantity(),
                        orderDao.getComment(),
                        clientDao.getStatus()
                ));
            }
        }

        this.clientsTable.setItems(clients);
        this.clientName.setText("");
        this.clientQuantity.setText("1");
        this.clientComment.setText("");
    }

    @FXML
    public void deleteClient() {
        try {
            // User confirms
            ConfirmDialog confirm = new ConfirmDialog()
                    .setTitle("Usuń klientów")
                    .setHeader("Usunąć zaznaczonych klientów?");
            if (confirm.showDialog()) {
//                // Get client ids
//                int[] clientIds = this.getSelectedClients().stream().mapToInt(ClientData::getClientId).toArray();
//                // Get model ids that correspond to clients in orderDaos
//                int[] modelIds = this.getSelectedClients().stream().mapToInt(ClientData::getModelId).toArray();
//                for (Transmission transmission : this.transmissions) {
//                    Order.deleteOrders(transmission.getId(), modelIds, clientIds);
//                }
                int[] orderIds = this.getSelectedClients().stream().mapToInt(DataDao::getOrderId).toArray();
                OrderDao[] orderDaos = Arrays.stream(orderIds).mapToObj(id -> {
                    try {
                        return OrderDao.getOrder(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).toArray(OrderDao[]::new);
                OrderDao.deleteOrders(orderDaos);
                this.clientsTable.getItems().removeAll(this.getSelectedClients());
                this.setModels();
            }
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "delete product");
        }
    }

    public ObservableList<DataDao> getSelectedClients() {
        return clientsTable.getSelectionModel().getSelectedItems();
    }

    @FXML
    public void tableKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.DELETE) {
            this.deleteClient();
        }
        else if (e.getCode() == KeyCode.F5) {
            clientsTable.refresh();
        }
    }

    @FXML
    public void textFiledKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            this.addClient();
        }
    }
}
