package com.ksiezyk.Daos;

import com.ksiezyk.DatabaseConnector.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDao {
    private static final String table = "clients";
    private int id;
    private final String name;
    private final int status;

    public ClientDao(String name) {
        this(0, name);
    }

    public ClientDao(int id, String name) {
        this.id = id;
        this.name = name;
        this.status = 0;
    }

    public static String getTable() {
        return table;
    }

    public static boolean findClient(ClientDao clientDao, String searchText) {
        return clientDao.getName().toLowerCase().contains(searchText.toLowerCase());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ObservableList<ClientDao> getClients() throws SQLException {
        ObservableList<ClientDao> clientDaos = FXCollections.observableArrayList();
        ResultSet result = DatabaseConnector.select(ClientDao.table);

        while (result.next()) {
            clientDaos.add(new ClientDao(result.getInt("id"), result.getString("name")));
        }

        return clientDaos;
    }

    public static ClientDao getClient(int id) throws SQLException {
        ResultSet result = DatabaseConnector.select(ClientDao.table, "id", id);

        if (result.next()) {
            return new ClientDao(result.getInt("id"), result.getString("name"));
        }
        else {
            throw new SQLException("Result set is empty after selecting client with id "
                    + Integer.toString(id));
        }
    }

    public void addToDatabase() throws SQLException {
        ResultSet result = DatabaseConnector.insert(this);
        if (result.next()) {
            this.id = result.getInt("id");
        }
        else {
            throw new SQLException("Result set is empty after inserting new client.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.name.equals(((ClientDao) obj).name);
    }
}
