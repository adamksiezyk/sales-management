module com.ksiezyk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.ksiezyk.Daos to javafx.base;
    opens com.ksiezyk.GUI to javafx.fxml;
    exports com.ksiezyk.GUI;
}