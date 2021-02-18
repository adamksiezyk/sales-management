package com.ksiezyk.Daos;

import com.ksiezyk.DatabaseConnector.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataDao {
    private final int orderId;
    private final int clientId;
    private final String clientName;
    private final int productId;
    private final String productName;
    private final int modelId;
    private final String modelName;
    private final double price;
    private final int quantity;
    private final String comment;
    private final int status;

    public DataDao(int orderId, int clientId, String clientName, int productId, String productName, int modelId,
                   String modelName, double price, int quantity, String comment, int status) {
        this.clientId = clientId;
        this.orderId = orderId;
        this.clientName = clientName;
        this.productId = productId;
        this.productName = productName;
        this.modelId = modelId;
        this.modelName = modelName;
        this.price = price;
        this.quantity = quantity;
        this.comment = comment;
        this.status = status;
    }

    public int getClientId() {
        return clientId;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getClientName() {
        return clientName;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getModelId() {
        return modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getComment() {
        return comment;
    }

    public int getStatus() {
        return status;
    }

    public static ObservableList<DataDao> getClientData(int transmissionId, int clientId) throws SQLException {
        ResultSet result = DatabaseConnector.selectDataDao(transmissionId, clientId);
        ObservableList<DataDao> datumDaos = FXCollections.observableArrayList();
        while (result.next()) {
            datumDaos.add(
                    new DataDao(
                            result.getInt("id_order"),
                            result.getInt("id_client"),
                            result.getString("name_client"),
                            result.getInt("id_product"),
                            result.getString("name_product"),
                            result.getInt("id_model"),
                            result.getString("name_model"),
                            result.getDouble("price"),
                            result.getInt("quantity"),
                            result.getString("comment"),
                            result.getInt("status")
                    )
            );
        }
        return datumDaos;
    }
}
