package com.ksiezyk.Daos;

import com.ksiezyk.DatabaseConnector.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TransmissionDao {
    private static final String table = "transmissions";
    private int id;
    private Timestamp date;

    public TransmissionDao() {
        this(0);
    }

    public TransmissionDao(int id) {
        this.id = id;
    }

    public TransmissionDao(int id, Timestamp date) {
        this.id = id;
        this.date = date;
    }

    public static String getTable() {
        return TransmissionDao.table;
    }

    public int getId() {
        return id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void addToDatabase() throws SQLException {
        ResultSet result = DatabaseConnector.insert(this);
        if (result.next()) {
            this.id = result.getInt("id");
        }
        else {
            throw new SQLException("Result set is empty after inserting new transmission.");
        }
    }

    public static TransmissionDao getTransmission(int transmissionId) throws SQLException {
        ResultSet result = DatabaseConnector.select(TransmissionDao.table, "id", transmissionId);
        if (result.next()) {
            return new TransmissionDao(
                    result.getInt("id"),
                    result.getTimestamp("date")
            );
        }
        return null;
    }

    public static ObservableList<TransmissionDao> getTransmissions() throws SQLException {
        ResultSet result = DatabaseConnector.select(TransmissionDao.table);
        ObservableList<TransmissionDao> transmissionDaos = FXCollections.observableArrayList();
        while (result.next()) {
            transmissionDaos.add(new TransmissionDao(
                    result.getInt("id"),
                    result.getTimestamp("date")));
        }
        return transmissionDaos;
    }

    public static void deleteTransmissions(int[] ids) throws SQLException {
        DatabaseConnector.delete(TransmissionDao.getTable(), ids);
    }
}
