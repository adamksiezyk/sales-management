package com.ksiezyk.Daos;

import com.ksiezyk.DatabaseConnector.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class ModelDao {
    private static final String table = "models";
    private int id;
    private final int productId;
    private final String name;
    private int quantity;

    public ModelDao(int productId, String name, int quantity) {
        this(0, productId, name, quantity);
    }

    public ModelDao(int id, int productId, String name, int quantity) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
    }

    public static String getTable() {
        return table;
    }

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return String.format("%s: %d", this.getName(), this.getQuantity());
    }

    public static ModelDao getModel(int id) throws SQLException {
        ResultSet resultSet = DatabaseConnector.select(ModelDao.table, "id", id);
        if (resultSet.next()) {
            return new ModelDao(
                    resultSet.getInt("id"),
                    resultSet.getInt("id_product"),
                    resultSet.getString("name"),
                    resultSet.getInt("quantity")
            );
        }
        throw new SQLException("no models found");
    }

    public static ObservableList<ModelDao> getModels(int productId) throws SQLException {
        ObservableList<ModelDao> modelDaos = FXCollections.observableArrayList();
        ResultSet resultSet = DatabaseConnector.select(ModelDao.table, "id_product", productId);
        while (resultSet.next()) {
            modelDaos.add(new ModelDao(
                    resultSet.getInt("id"),
                    resultSet.getInt("id_product"),
                    resultSet.getString("name"),
                    resultSet.getInt("quantity")
            ));
        }
        return modelDaos;
    }

    public void addModel() throws SQLException {
        ResultSet resultSet = DatabaseConnector.insert(this);
        if (resultSet.next()) {
            this.id = resultSet.getInt("id");
        }
        else {
            throw new SQLException("Result set is empty after inserting new model.");
        }
    }

    public static void deleteModel(ObservableList<ModelDao> modelDaos)
            throws SQLException, SQLIntegrityConstraintViolationException {
        int[] modelIds = modelDaos.stream().mapToInt(ModelDao::getId).toArray();
        DatabaseConnector.delete(ModelDao.table, modelIds);
    }

    public void changeQuantity(int amount) throws SQLException, ArithmeticException {
        if (this.quantity + amount < 0) {
            throw new ArithmeticException("quantity can't be a negative number");
        }
        this.quantity += amount;
        DatabaseConnector.update(this);
    }

    public static void editModel(int id, String name, int quantity) throws SQLException {
        DatabaseConnector.update(new ModelDao(id, 0, name, quantity));
    }
}
