package com.ksiezyk.Daos;

import com.ksiezyk.DatabaseConnector.DatabaseConnector;
import com.ksiezyk.GUI.ErrorDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

public class ProductDao {
    private static final String table = "products";
    private final int id;
    private final String name;
    private final String code;
    private final double price;
    private final int status;
    private final int magazine;

    public ProductDao(String name, String code, double price) {
        this(0, name, code, price);
    }

    public ProductDao(String name, String code, double price, int status, int magazine) {
        this(0, name, code, price, status, magazine);
    }

    public ProductDao(int id, String name, String code, double price) {
        this(id, name,code, price, 1, 1);
    }

    public ProductDao(int id, String name, String code, double price, int status, int magazine) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.price = price;
        this.status = status;
        this.magazine = magazine;
    }

    public static String getTable() {
        return table;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public double getPrice() {
        return price;
    }

    public int getStatus() {
        return status;
    }

    public int getMagazine() {
        return magazine;
    }

    public static ObservableList<ProductDao> getProducts() throws SQLException {
        ObservableList<ProductDao> productDaos = FXCollections.observableArrayList();
        ResultSet result = DatabaseConnector.select(ProductDao.table);

        while (result.next()) {
            productDaos.add(new ProductDao(result.getInt("id"), result.getString("name"),
                    result.getString("code"), result.getFloat("price")));
        }

        return productDaos;
    }

    public static ProductDao getProduct(int productId) throws SQLException {
        ResultSet result = DatabaseConnector.select(ProductDao.table, "id", productId);

        if (result.next()) {
            return new ProductDao(result.getInt("id"), result.getString("name"),
                    result.getString("code"), result.getFloat("price"));
        }

        return null;
    }

    public static void addProduct(ProductDao productDao) throws SQLException {
        // Check if product already exists
        if (DatabaseConnector.checkIfExists(ProductDao.table, "name", productDao.getName()) != 0) {
            ErrorDialog.showErrorDialog("Duplikat", "Produkt już istnieje",
                    "Produkt o podanej nazwie już istnieje");
            return;
        }
        else if (DatabaseConnector.checkIfExists(ProductDao.table, "code", productDao.getCode()) != 0) {
            ErrorDialog.showErrorDialog("Duplikat", "Produkt już istnieje",
                    "Produkt o podanym kodzie już istnieje");
            return;
        }
        // Add to database
        DatabaseConnector.insert(productDao);
    }

    public static void deleteProduct(ObservableList<ProductDao> productDaos)
            throws SQLException, SQLIntegrityConstraintViolationException {
        int[] productsIds = productDaos.stream().mapToInt(ProductDao::getId).toArray();
        DatabaseConnector.delete(ProductDao.table, productsIds);
    }

    public static void editProduct(int id, String name, String code, double price) throws SQLException {
        DatabaseConnector.update(new ProductDao(id, name, code, price));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDao productDao = (ProductDao) o;
        return id == productDao.id && Double.compare(productDao.price, price) == 0 && Objects.equals(name, productDao.name) && Objects.equals(code, productDao.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, price);
    }

    public static boolean findProduct(ProductDao productDao, String searchText) {
        return productDao.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                productDao.getCode().toLowerCase().contains(searchText.toLowerCase());
    }
}
