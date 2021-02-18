package com.ksiezyk.GUI;

import com.ksiezyk.Daos.ProductDao;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.function.Predicate;

public class MagazineController {
    public TextField nameField;
    public TextField codeField;
    public TextField priceField;
    public TableView<ProductDao> productsTable;
    public ObservableList<ProductDao> productDaos;
    public FilteredList<ProductDao> filteredProductDaos;
    public TextField searchField;

    // Add product
    @FXML
    void addProduct() {
        // Product details
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String priceString = priceField.getText().replace(',', '.');
        // Check if not empty
        if (priceString.isEmpty() || (name.isEmpty() && code.isEmpty())) {
            ErrorDialog.showErrorDialog("Błąd danych", "Błąd podczas dodawania produktu",
                    "Uzupełnij wymagane puste pola:\n- nazwa lub kod produktu\n- cena");
            return;
        }
        double price  = Double.parseDouble(priceString);
        ProductDao productDao = new ProductDao(name, code, price);
        try {
            ProductDao.addProduct(productDao);
            updateTable();
        }
        catch (Exception e1) {
            ErrorDialog.showErrorDialog(e1, "add product");
        }
    }

    public void updateTable() throws SQLException {
        this.productDaos = ProductDao.getProducts();
        productsTable.setItems(productDaos);
        nameField.setText("");
        codeField.setText("");
        priceField.setText("");
        searchField.setText("");
        nameField.requestFocus();
    }

    @FXML
    public void refreshWindow(KeyEvent e) {
        if (e.getCode() == KeyCode.F5) {
            try {
                updateTable();
            }
            catch (Exception e1) {
                ErrorDialog.showErrorDialog(e1, "refresh window");
            }
        }
    }

    @FXML
    public void tableKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.DELETE) {
            this.deleteProduct();
        }
        else if (e.getCode() == KeyCode.F2) {
            this.editProduct();
        }
    }

    @FXML
    public void deleteProduct() {
        try {
            // Confirm
            ConfirmDialog confirm = new ConfirmDialog()
                    .setTitle("Usuń produkty")
                    .setHeader("Usunąć zaznaczone produkty?");
            if (confirm.showDialog()) {
                ProductDao.deleteProduct(this.getSelectedProducts());
                updateTable();
            }
        }
        catch (SQLIntegrityConstraintViolationException e) {
            ErrorDialog.showErrorDialog("Błąd danych", "Nie można usunąć produktu",
                    "Nie można usunąć produktu, ponieważ istnieje zamówienie na ten produkt.");
        }
        catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "delete product");
        }
    }

    @FXML
    public void editProduct() {
        try {
            Stage magazineStage = this.getStage();
            ProductDao selectedProductDao = this.getSelectedProduct();
            new ProductEditor(magazineStage, selectedProductDao);
            updateTable();
        }
        catch (Exception e1) {
            ErrorDialog.showErrorDialog(e1, "edit product");
        }
    }

    @FXML
    public void addProductToTransmission() {
        this.getStage().close();
    }

    public ProductDao getSelectedProduct() {
        return this.productsTable.getSelectionModel().getSelectedItem();
    }

    public ObservableList<ProductDao> getSelectedProducts() {
        return productsTable.getSelectionModel().getSelectedItems();
    }

    public Stage getStage() {
        return (Stage) this.productsTable.getScene().getWindow();
    }

    public void setItems() throws SQLException {
        // Select products
        this.productDaos = ProductDao.getProducts();
        this.filteredProductDaos = new FilteredList<>(this.productDaos);
        // Set items
        productsTable.setItems(this.filteredProductDaos);
        // Add filter
        this.searchField.textProperty().addListener((observable, oldValue, newValue) ->
                this.filteredProductDaos.setPredicate(createPredicate(newValue))
        );
    }

    private Predicate<ProductDao> createPredicate(String searchText){
        return product -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return ProductDao.findProduct(product, searchText);
        };
    }

    @FXML
    public void textFieldKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            this.addProduct();
        }
    }
}
