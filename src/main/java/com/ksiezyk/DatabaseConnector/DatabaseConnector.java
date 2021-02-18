package com.ksiezyk.DatabaseConnector;

import com.ksiezyk.Daos.*;

import java.sql.*;
import java.util.Locale;


// Database management
public class DatabaseConnector {
    private static final String server = "localhost";
    private static final String port = "3306";
    private static final String user = "java_connector";
    private static final String password = "Dupa1234";
    private static final String database = "transmisja";
    private static Connection connection;

    private DatabaseConnector() throws SQLException {
        if (connection == null || connection.isClosed()){
            connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:%s/%s?serverTimezone=Europe/Warsaw",
                            server, port, database), user, password);
        }
    }

    public static ResultSet select(String table) throws SQLException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT * FROM " + table);
    }

    public static ResultSet select(String table, String column, int id) throws SQLException {
        new DatabaseConnector();
        if (id == 0) {return select(table);}
        PreparedStatement stmt = connection.prepareStatement(String.format(
                "SELECT * FROM %s WHERE %s = ?", table, column));
        stmt.setInt(1, id);
        return stmt.executeQuery();
    }

    public static void insert(ProductDao... productDaos) throws SQLException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        StringBuilder sql = new StringBuilder(String.format("INSERT INTO %s VALUES ", ProductDao.getTable()));
        for (ProductDao productDao : productDaos) {
            sql.append(String.format(Locale.US, "('%d', '%s', '%s', '%.2f', %d, %d),", productDao.getId(),
                    productDao.getName(), productDao.getCode(), productDao.getPrice(), productDao.getStatus(),
                    productDao.getMagazine()));
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 1));
        statement.executeUpdate(sql.toString());
    }

    public static ResultSet insert(ClientDao clientDao) throws SQLException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        String sql = String.format("INSERT INTO %s (id, name) VALUES ('%d', '%s')",
                ClientDao.getTable(), clientDao.getId(), clientDao.getName());
        statement.executeUpdate(sql);
        return statement.executeQuery(String.format(
                "SELECT id FROM %s ORDER BY id DESC LIMIT 1", ClientDao.getTable()));
    }

    public static ResultSet insert(TransmissionDao transmissionDao) throws SQLException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.format("INSERT INTO %s VALUES ()", TransmissionDao.getTable()));
        return statement.executeQuery(String.format(
                "SELECT id FROM %s ORDER BY id DESC LIMIT 1", TransmissionDao.getTable()));
    }

    public static ResultSet insert(OrderDao orderDao) throws SQLException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.format("INSERT INTO %s " +
                        "(id, id_transmission, id_product, id_model, id_client, quantity, comment) " +
                        "VALUES (%d, %d, %d, %d, %d, %d, '%s')",
                OrderDao.getTable(), orderDao.getId(), orderDao.getTransmissionId(), orderDao.getProductId(),
                orderDao.getModelId(), orderDao.getClientId(), orderDao.getQuantity(), orderDao.getComment()));
        return statement.executeQuery(String.format(
                "SELECT id FROM %s ORDER BY id DESC LIMIT 1", OrderDao.getTable()));
    }

    public static ResultSet insert(ModelDao modelDao) throws SQLException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.format("INSERT INTO %s (id, id_product, name, quantity) " +
                        "VALUES (%d, %d, '%s', %d)",
                ModelDao.getTable(), modelDao.getId(), modelDao.getProductId(), modelDao.getName(), modelDao.getQuantity()));
        return statement.executeQuery(String.format(
                "SELECT id FROM %s ORDER BY id DESC LIMIT 1", ModelDao.getTable()));
    }

    public static void delete(String table, int... ids)
            throws SQLException, SQLIntegrityConstraintViolationException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        // Check if id is valid
        if (ids.length == 0 || ids[0] == 0) { return; }
        String sql = String.format("DELETE FROM %s WHERE id in (", table);
        for (int id : ids) {
            sql += String.format("%d,", id);
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        statement.executeUpdate(sql);
    }

    public static void delete(String table, String column1, int id1, String column2, int... ids2)
            throws SQLException, SQLIntegrityConstraintViolationException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        // Check if id is valid
        if (ids2.length == 0 || id1 == 0) { return; }
        String sql = String.format("DELETE FROM %s WHERE %s = %s AND %s in (",
                table, column1, id1, column2);
        for (int id : ids2) {
            sql += String.format("%d,", id);
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        statement.executeUpdate(sql);
    }

    public static void delete(String table, String column1, int id1, String column2, int[] ids2, String column3,
                              int[] ids3) throws SQLException, SQLIntegrityConstraintViolationException {
        new DatabaseConnector();
        Statement statement = connection.createStatement();
        // Check if id is valid
        if (ids3.length == 0 || ids3.length != ids2.length || id1 == 0) { return; }
        String sql = String.format("DELETE FROM %s WHERE %s = %s AND (",
                table, column1, id1);
        for (int i = 0; i < ids3.length; i++) {
            sql += String.format("(%s = %d AND %s = %d) OR ", column2, ids2[i], column3, ids3[i]);
        }
        sql = sql.substring(0, sql.length() - 3);
        sql += ")";
        statement.executeUpdate(sql);
    }

    public static void update(ProductDao productDao) throws SQLException {
        new DatabaseConnector();
        if (productDao == null || productDao.getId() == 0) { return; }
        PreparedStatement stmt = connection.prepareStatement(
                "UPDATE products SET name = ?, code = ?, price = ? WHERE id = ?");
        stmt.setString(1, productDao.getName());
        stmt.setString(2, productDao.getCode());
        stmt.setDouble(3, productDao.getPrice());
        stmt.setInt(4, productDao.getId());
        stmt.executeUpdate();
    }

    public static void update(ClientDao clientDao) throws SQLException {
        new DatabaseConnector();
        if (clientDao == null || clientDao.getId() == 0) { return; }
        PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clients SET name = ?, status = ? WHERE id = ?");
        stmt.setString(1, clientDao.getName());
        stmt.setInt(2, clientDao.getStatus());
        stmt.setInt(3, clientDao.getId());
        stmt.executeUpdate();
    }

    public static void update(ModelDao modelDao) throws SQLException {
        new DatabaseConnector();
        if (modelDao == null || modelDao.getId() == 0) { return; }
        PreparedStatement stmt = connection.prepareStatement(
                "UPDATE models SET name = ?, quantity = ? WHERE id = ?");
        stmt.setString(1, modelDao.getName());
        stmt.setInt(2, modelDao.getQuantity());
        stmt.setInt(3, modelDao.getId());
        stmt.executeUpdate();
    }

    public static int checkIfExists(String table, String column, String value) throws SQLException {
        String sql = String.format("SELECT id FROM %s WHERE %s = ?", table, column);
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, value);
        ResultSet result = stmt.executeQuery();
        if (result.next()) {
            return result.getInt(1);
        }
        return 0;
    }

    public static ResultSet selectDataDao(int transmissionId, int clientId) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "select " +
                        "o.id as 'id_order', " +
                        "c.id as 'id_client', " +
                        "c.name as 'name_client', " +
                        "p.id as 'id_product', " +
                        "p.name as 'name_product', " +
                        "m.id as 'id_model', " +
                        "m.name as 'name_model', " +
                        "p.price, " +
                        "o.quantity, " +
                        "o.comment, " +
                        "c.status " +
                "from " +
                        "orders o inner join products p " +
                            "on o.id_product = p.id " +
                        "inner join models m " +
                            "on o.id_model = m.id " +
                        "inner join clients c " +
                            "on o.id_client = c.id " +
                "where " +
                        "o.id_transmission = ? and o.id_client = ? " +
                "order by c.name asc;");
        stmt.setInt(1, transmissionId);
        stmt.setInt(2, clientId);
        return stmt.executeQuery();
    }
}
