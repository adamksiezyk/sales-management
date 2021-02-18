package com.ksiezyk.Daos;

import com.ksiezyk.DatabaseConnector.DatabaseConnector;
import com.ksiezyk.GUI.ErrorDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.IntStream;

public class OrderDao {
    private static final String table = "orders";
    private int id;
    private final int transmissionId;
    private final int productId;
    private final int modelId;
    private final int clientId;
    private final int quantity;
    private final String comment;

    public OrderDao(int transmissionId,
                    int productId,
                    int modelId,
                    int clientId,
                    int quantity,
                    String comment) {
        this(0, transmissionId, productId, modelId, clientId, quantity, comment);
    }

    public OrderDao(int id,
                    int transmissionId,
                    int productId,
                    int modelId,
                    int clientId,
                    int quantity,
                    String comment) {
        this.id = id;
        this.transmissionId = transmissionId;
        this.productId = productId;
        this.modelId = modelId;
        this.clientId = clientId;
        this.quantity = quantity;
        this.comment = comment;
    }

    public static String getTable() {
        return table;
    }

    public int getId() {
        return id;
    }

    public int getTransmissionId() {
        return transmissionId;
    }

    public int getProductId() {
        return productId;
    }

    public int getModelId() {
        return modelId;
    }

    public int getClientId() {
        return clientId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getComment() {
        return comment;
    }

    public void addToDatabase() throws SQLException, ArithmeticException {
        ModelDao.getModel(this.modelId).changeQuantity(-this.quantity);
        ResultSet result = DatabaseConnector.insert(this);
        if (result.next()) {
            this.id = result.getInt("id");
        }
        else {
            throw new SQLException("Result set is empty after inserting new order.");
        }
    }

    public static ObservableList<OrderDao> getOrders(int transmissionId) throws SQLException {
        ObservableList<OrderDao> orderDaos = FXCollections.observableArrayList();
        ResultSet result = DatabaseConnector.select(OrderDao.table, "id_transmission", transmissionId);

        while (result.next()) {
            orderDaos.add(new OrderDao(
                    result.getInt("id"),
                    result.getInt("id_transmission"),
                    result.getInt("id_product"),
                    result.getInt("id_model"),
                    result.getInt("id_client"),
                    result.getInt("quantity"),
                    result.getString("comment")
            ));
        }

        return orderDaos;
    }

    public static void deleteOrders(OrderDao[] orderDaos) throws SQLException {
        // Delete orderDaos
        DatabaseConnector.delete(OrderDao.table, Arrays.stream(orderDaos).mapToInt(OrderDao::getId).toArray());
        // Increase models quantity
        for (OrderDao orderDao : orderDaos) {
            try {
                ModelDao.getModel(orderDao.getModelId()).changeQuantity(orderDao.getQuantity());
            } catch (SQLException e) {
                ErrorDialog.showErrorDialog(e, "delete orderDaos");
            }
        }
    }

    public static void deleteOrdersByProductId(int transmissionId, int... productIds) throws SQLException {
        ObservableList<OrderDao> orderDaos = OrderDao.getOrders(transmissionId);
        for (OrderDao orderDao : orderDaos) {
            if (IntStream.of(productIds).anyMatch(id -> id == orderDao.getProductId())) {
                ModelDao.getModel(orderDao.getModelId()).changeQuantity(orderDao.getQuantity());
            }
        }
        DatabaseConnector.delete(OrderDao.table, "id_transmission", transmissionId, "id_product", productIds);
    }

    public static void deleteOrdersByClientId(int transmissionId, int... clientIds) throws SQLException {
        ObservableList<OrderDao> orderDaos = OrderDao.getOrders(transmissionId);
        for (OrderDao orderDao : orderDaos) {
            if (IntStream.of(clientIds).anyMatch(id -> id == orderDao.getClientId())) {
                ModelDao.getModel(orderDao.getModelId()).changeQuantity(orderDao.getQuantity());
            }
        }
        DatabaseConnector.delete(OrderDao.table, "id_transmission", transmissionId, "id_client", clientIds);
    }

    public static void deleteOrders(int transmissionId, int[] modelIds, int[] clientIds) throws SQLException {
        ObservableList<OrderDao> orderDaos = OrderDao.getOrders(transmissionId);
        for (OrderDao orderDao : orderDaos) {
            if (IntStream.of(modelIds).anyMatch(id -> id == orderDao.getModelId()) &&
                    IntStream.of(clientIds).anyMatch(id -> id == orderDao.getClientId())) {
                ModelDao.getModel(orderDao.getModelId()).changeQuantity(orderDao.getQuantity());
            }
        }
        DatabaseConnector.delete(OrderDao.table, "id_transmission", transmissionId, "id_model",
                modelIds, "id_client", clientIds);
    }

    public static OrderDao getOrder(int id) throws SQLException {
        ResultSet result = DatabaseConnector.select(OrderDao.table, "id", id);
        if (result.next()) {
            return new OrderDao(
                    result.getInt("id"),
                    result.getInt("id_transmission"),
                    result.getInt("id_product"),
                    result.getInt("id_model"),
                    result.getInt("id_client"),
                    result.getInt("quantity"),
                    result.getString("comment")
            );
        }
        return null;
    }
}
