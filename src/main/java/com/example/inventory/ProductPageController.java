package com.example.inventory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class ProductPageController implements Initializable {

    @FXML
    private TextField searchPartTextField, productPriceField, productMaxField, productMinField, productNameTextField, productLnvField, productIDTextField;

    @FXML
    TableView<Part> partsTable, partsTable1;

    @FXML
    private TableColumn<Part, Integer> partsID, partsLevel, partsID1, partsLevel1;

    @FXML
    private TableColumn<Part, Double> partsCost, partsCost1;

    @FXML
    private TableColumn<Part, String> partsName, partsName1;

    private HomeController documentController;
    Part partSelected, selectedUnconnectedPart;
    ObservableList<Part> productParts = FXCollections.observableArrayList();


    /**
     * Dissociates the selected part from the current product
     */
    @FXML
    void productPartDeAssociateAction(ActionEvent event) {
        //when a part is selected
        if (partSelected != null) {
            // showing the causing message and confirm dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Caution!");
            alert.setHeaderText("Are you sure you want to Dissociate " + partSelected.getName() + " From this Product?");
            alert.setContentText(null);

            Optional<ButtonType> result = alert.showAndWait();
            // when confirmed
            if (result.get() == ButtonType.OK) {
                // delete parts from products table and deassociate them
                productParts.remove(partSelected);
                partsTable.getItems().setAll(productParts);
            }
        } else {
            // when no part is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error!");
            alert.setHeaderText("Please, Select at-least one product to perform delete operation!");
            alert.setContentText(null);

            alert.showAndWait();
        }

    }

    /**
     * Closes the Add Product form
     */
    @FXML
    void closeButtonAction(ActionEvent event) {
        // Close current window
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }

    /**
     * Adds a part into the current product associate list
     */
    @FXML
    private void addProductAssociatedPart(ActionEvent event) {
        if (selectedUnconnectedPart != null) {
            if (!productParts.contains(selectedUnconnectedPart)) {
                productParts.add(selectedUnconnectedPart);
                partsTable.getItems().setAll(productParts);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error!");
                alert.setHeaderText("The part is already associated");
                alert.setContentText(null);
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error!");
            alert.setHeaderText("Please, Select at least one part to add!");
            alert.setContentText(null);
            alert.showAndWait();
        }
    }

    /**
     * Validates the input and adds the product data to th system data
     */
    @FXML
    void productSaveButtonAction(ActionEvent event) {
        try {
            if (!(productNameTextField.getText().isEmpty() || productLnvField.getText().isEmpty() || productPriceField.getText().isEmpty() || productMaxField.getText().isEmpty() || productMinField.getText().isEmpty())) {
                // If the inventory level is lower then max
                if (Integer.parseInt(productMaxField.getText()) > Integer.parseInt(productMinField.getText())) {
                    // If max is lower then min
                    if (Integer.parseInt(productLnvField.getText()) < Integer.parseInt(productMaxField.getText())) {
                        // if add new product button was pressed
                        // add the new part to the main arraylist
                        documentController.addNewProduct(Integer.parseInt(productIDTextField.getText()), productNameTextField.getText(), Integer.parseInt(productLnvField.getText()), Double.parseDouble(productPriceField.getText()), Integer.parseInt(productMaxField.getText()), Integer.parseInt(productMinField.getText()), productParts);

                        // Show successfully new part addition dialog
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success!");
                        alert.setHeaderText("Successfully Added new Product!");
                        alert.setContentText(null);
                        alert.showAndWait();

                        // Closing the window after the save has been successfully finished
                        final Node source = (Node) event.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else {
                        // Displays message when Max value can not be lower than inventory level value
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error!");
                        alert.setHeaderText("Max value can not be lower than inventory level value!");
                        alert.setContentText(null);
                        alert.showAndWait();
                    }
                } else {
                    // Displays message when Max value can not be lower than min value
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Max value can not be lower than min value!");
                    alert.setContentText(null);
                    alert.showAndWait();
                }
            } else {
                // Displays message when user submits incomplete form
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("All fields required");
                alert.setContentText(null);
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            //Displays message when user enters invalid inputs
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Invalid input. Lnv, Price, Min and Max should be number. Kindly check and try again");
            alert.setContentText(null);
            alert.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs a search in the parts table
     */
    @FXML
    void partsSearch() {
        ObservableList<Part> filteredData = Inventory.lookupPart(searchPartTextField.getText());
        // Wrap the FilteredList in a SortedList.
        SortedList<Part> sortedData = new SortedList<>(filteredData);
        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(partsTable1.comparatorProperty());
        // Add sorted (and filtered) data to the table.
        partsTable1.setItems(sortedData);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // setting add new parts table collumns cell value factory
        partsID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partsLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsCost.setCellValueFactory(new PropertyValueFactory<>("price"));
        partsName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // setting add product associated parts table collumns cell value factory
        partsID1.setCellValueFactory(new PropertyValueFactory<>("id"));
        partsLevel1.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsCost1.setCellValueFactory(new PropertyValueFactory<>("price"));
        partsName1.setCellValueFactory(new PropertyValueFactory<>("name"));
        // setting add click event for parts selection for future delete or modify action
        partsTable.setOnMousePressed(event -> partSelected = partsTable.getSelectionModel().getSelectedItem());
        partsTable1.setOnMousePressed(event -> selectedUnconnectedPart = partsTable1.getSelectionModel().getSelectedItem());

        partsTable1.getItems().setAll(Inventory.getAllParts());
    }


    /**
     * Default parent controller class setter method
     */
    void setParentController(HomeController documentController) {
        this.documentController = documentController;
    }

    /**
     * setting auto generated id for new product
     */
    void setData(int generateProductsID) {
        productIDTextField.setText("" + generateProductsID);
    }

}
