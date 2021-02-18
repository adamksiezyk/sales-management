package com.ksiezyk.GUI;

import com.ksiezyk.Daos.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.SQLException;

public class ShowClientProductsController {
    public TableView<DataDao> productsTable;
    private ObservableList<TransmissionDao> transmissionDaos;
    private ClientDao clientDao;

    public void setTransmissions(ObservableList<TransmissionDao> transmissionDaos) {
        this.transmissionDaos = transmissionDaos;
    }

    public void setClient(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public void updateTable() {
        if (this.transmissionDaos == null || this.transmissionDaos.isEmpty() || this.clientDao == null) return;

        try {
            ObservableList<OrderDao> orderDaos = FXCollections.observableArrayList();
            for (TransmissionDao transmissionDao : this.transmissionDaos) {
                orderDaos.addAll(OrderDao.getOrders(transmissionDao.getId()));
            }
            ObservableList<DataDao> data = FXCollections.observableArrayList();
            for (OrderDao orderDao : orderDaos) {
                if (orderDao.getClientId() == this.clientDao.getId()) {
                    ProductDao productDao = ProductDao.getProduct(orderDao.getProductId());
                    data.add(new DataDao(
                            orderDao.getId(),
                            this.clientDao.getId(),
                            this.clientDao.getName(),
                            productDao.getId(),
                            productDao.getName(),
                            orderDao.getModelId(),
                            ModelDao.getModel(orderDao.getModelId()).getName(),
                            productDao.getPrice(),
                            orderDao.getQuantity(),
                            orderDao.getComment(),
                            this.clientDao.getStatus()
                    ));
                }
            }
            this.productsTable.setItems(data);
        }
        catch (SQLException e) {
            ErrorDialog.showErrorDialog(e, "show client products");
        }
    }

    @FXML
    public void deleteProduct() {
        if (this.transmissionDaos == null || this.transmissionDaos.isEmpty() || this.clientDao == null) return;

        try {
            // User confirms
            ConfirmDialog confirm = new ConfirmDialog()
                    .setTitle("Usuń produkty")
                    .setHeader("Usunąć zaznaczone produkty?");
            if (confirm.showDialog()) {
                int[] modelIds = this.getSelectedProducts().stream().mapToInt(DataDao::getModelId).toArray();
                for (TransmissionDao transmissionDao : this.transmissionDaos) {
                    OrderDao.deleteOrders(transmissionDao.getId(), modelIds, new int[]{clientDao.getId()});
                }
                this.updateTable();
            }
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "delete product");
        }
    }

    public ObservableList<DataDao> getSelectedProducts() {
        return productsTable.getSelectionModel().getSelectedItems();
    }

    @FXML
    public void tableKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.DELETE) {
            this.deleteProduct();
        }
        else if (e.getCode() == KeyCode.F5) {
            updateTable();
        }
    }
}
