package com.example.inventory;


import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

// It's the Main/First window controller class
public class HomeController implements Initializable {

    /*----------------------------------------All FXML Button, Field, RadioButton,Label Declaration-----------------------*/
    @FXML
    TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> productID, productLevel;

    @FXML
    private TableColumn<Product, Double> productCost;

    @FXML
    private TableColumn<Product, String> productName;

    @FXML
    private TextField productFilterString;

    @FXML
    TableView<Part> partsTable;

    @FXML
    private TableColumn<Part, Integer> partsID, partsLevel;

    @FXML
    private TableColumn<Part, Double> partsCost;

    @FXML
    private TableColumn<Part, String> partsName;

    @FXML
    private TextField partsFilterString;

    /*----------------------------------------All FXML Button, Field, RadioButton,Label Declaration-----------------------*/

    Part partSelected = null;
    Product productSelected = null;

    // Runs at the start of the windows life circle
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            prepareData("inventory_data.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadData();
    }

    /**
     * Prepares the tables to populate them with the available startup data
     */
    public void loadData() {

        productTable.getItems().clear();
        productTable.getItems().setAll(Inventory.getAllProducts());
        productID.setCellValueFactory(new PropertyValueFactory<>("id"));
        productLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productCost.setCellValueFactory(new PropertyValueFactory<>("price"));
        productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        productTable.setOnMousePressed(event -> productSelected = productTable.getSelectionModel().getSelectedItem());

        partsTable.getItems().clear();
        partsTable.getItems().setAll(Inventory.getAllParts());
        partsID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partsLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsCost.setCellValueFactory(new PropertyValueFactory<>("price"));
        partsName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partsTable.setOnMousePressed(event -> partSelected = partsTable.getSelectionModel().getSelectedItem());
    }

    /**
     * Opens up the add product form
     */
    @FXML
    void partAddButtonAction(ActionEvent event) {

        // Load "Add Part" interface
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("part-page.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Part Window");
            stage.setScene(new Scene(root));
            loader.<PartPageController>getController()
                    .setParentController(this);
            PartPageController api = loader.getController();
            api.setID(generatePartsID());
            stage.show();

        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error!");
            alert.setHeaderText("Something went wrong");
            alert.setContentText(null);
            alert.showAndWait();
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Closes the application
     */
    @FXML
    void closeButtonAction(ActionEvent event) {
        // Close current window
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }

    /**
     * Opens the part modify form populated with the details of the selected part from the parts table
     *
     * the modifications are save in the respective row  in the inventory_data.csv file
     */
    @FXML
    void partsModifyButtonAction(ActionEvent event) {

        // Checking is any parts on table is selected

        //TODO  AM DONE HERE
        if (partSelected != null) {
            try {
                // Loading to modify part window
                FXMLLoader loader = new FXMLLoader(getClass().getResource("part-modify-page.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Modify Part Window");

                stage.setScene(new Scene(root));
                loader.<PartModifyPageController>getController()
                        .setParentController(this);

                PartModifyPageController api = loader.getController();
                // Sending the selected part object to modify part class for operation
                api.setData(partSelected);
                stage.show();

            } catch (IOException ex) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Error!");
                alert.setHeaderText("Something went wrong");
                alert.setContentText(null);
                alert.showAndWait();
                Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // When no part was selected show alert to avoid exception
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error!");
            alert.setHeaderText("Please, Select a part to modify");
            alert.setContentText(null);
            alert.showAndWait();
        }
    }

    /**
     * Deletes the details of the selected part
     *
     * this  record  removed permanently from the respective row  in the inventory_data.csv file
     */
    @FXML
    void deletePartsAction(ActionEvent event) {
        Product associatedProduct = isAssociated(partSelected);
        //TODO AM DONE HERE

        // Checking If the one part is selected, and it is not associated with any products
        if (partSelected != null && associatedProduct == null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation!");
            alert.setHeaderText("Are you sure you want to delete : " + partSelected.getName() + "?");
            alert.setContentText(null);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // Upon conformation remove the selected part from the table
                Inventory.deletePart(partSelected);
                loadData();
            }
        }
        // When a part is selected, but it is associated with a product
        else if (partSelected != null) {

            //Asks the user to proceed to the product page and dissociate the part before deleting it
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Error!");
            alert.setHeaderText("Selected part is associated with Product " + associatedProduct.getName() + "\nCan't delete the item directly, Please, Go to the modify part section.");
            alert.setContentText(null);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                try {
                    // When user agrees the product modify form is loaded
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("product-modify-page.fxml")); // Loading the modify product window for modification
                    Parent root = (Parent) loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Update Product Window");
                    stage.setScene(new Scene(root));
                    loader.<ProductModifyPageController>getController()
                            .setParentController(this);

                    ProductModifyPageController api = loader.getController();
                    api.setData(associatedProduct);
                    api.partsTable.getItems().setAll(associatedProduct.getAllAssociatedParts());
                    stage.show();

                } catch (IOException ex) {
                    Alert alert1 = new Alert(AlertType.WARNING);
                    alert1.setTitle("Error!");
                    alert1.setHeaderText("Something went wrong");
                    alert1.setContentText(null);
                    alert1.showAndWait();
                    Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error!");
            alert.setHeaderText("Please, Select at-least one part to perform delete operation!");
            alert.setContentText(null);

            alert.showAndWait();
        }
    }

    /**
     * Performs the search in parts table
     */
    @FXML
    void partsSearch() {
        if (!partsFilterString.getText().isEmpty()) {
            ObservableList<Part> result = Inventory.lookupPart(partsFilterString.getText());
            partsTable.setItems(result);
        }

    }

    /**
     * Opens up the add product form
     */
    @FXML
    void productAddButtonAction(ActionEvent event) {

        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("product-page.fxml"));    // Load the AddProductform
            Parent root1 = loader1.load();
            Stage stage1 = new Stage();
            stage1.setTitle("Add Product Window");
            stage1.setScene(new Scene(root1));
            loader1.<ProductPageController>getController()
                    .setParentController(this);
            ProductPageController api = loader1.getController();
            api.setData(generateProductsID());
            stage1.show();

        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error!");
            alert.setHeaderText("Something went wrong");
            alert.setContentText(null);
            alert.showAndWait();
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Opens the modify product form populated with details of the selected product from the table
     *
     * the modifications are save in the respective row  in the inventory_data.csv file
     */
    @FXML
    void productModifyButtonAction(ActionEvent event) {

        if (productSelected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("product-modify-page.fxml")); // Load the add product UI
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Update Product Window");

                stage.setScene(new Scene(root));
                loader.<ProductModifyPageController>getController()
                        .setParentController(this);

                ProductModifyPageController api = loader.getController();
                // Set the selected products data to the add product interface to autofill all the fields
                api.setData(productSelected);

                // Show the tableView with all associated parts
                api.partsTable.getItems().setAll(productSelected.getAllAssociatedParts());
                api.partsTable1.getItems().setAll(Inventory.getAllParts());
                stage.show();

            } catch (IOException ex) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Error!");
                alert.setHeaderText("Something went wrong");
                alert.setContentText(null);
                alert.showAndWait();
                Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error!");
            alert.setHeaderText("Please, Select at least one part to modify!");
            alert.setContentText(null);

            alert.showAndWait();
        }
    }

    /**
     * Generates the unique ID for the parts
     */
    int generatePartsID() {
        int a = 1;

        for (Part o : Inventory.getAllParts()) {
            if (o.getId() >= a) {
                a = o.getId() + 1;
            }
        }

        return a;
    }

    /**
     * Generates the unique ID for the products
     */
    int generateProductsID() {
        int a = 1;
        for (Product o : Inventory.getAllProducts()) {
            if (o.getId() >= a) {
                a = o.getId() + 1;
            }
        }
        return a;
    }

    /**
     * Deletes the selected product
     *
     * this  record  removed permanently from the respective row  in the inventory_data.csv file
     */
    @FXML
    void deleteProductSelected(ActionEvent event) {

        //TODO check the modifications i made here

        // If any product is selected
        if (productSelected != null) {

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation!");
            alert.setHeaderText("Are you sure you want to delete " + productSelected.getName() + "?");
            StringBuilder warningText = new StringBuilder("Deleting this product will also dissociate parts:");

            // Showing all the parts associated with the part deleting in the warning
            for (Part p : productSelected.getAllAssociatedParts()) {
                warningText.append("\nProduct ID : ").append(p.getId());
            }
            alert.setContentText(warningText.toString());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Inventory.deleteProduct(productSelected);

                // Read the contents of the CSV file into a list
                List<String> lines = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader("inventory_data.csv"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Remove the record corresponding to the deleted product
                String productRecord = "Product," + productSelected.getId() + ",";
                lines.removeIf(line -> line.startsWith(productRecord));

                // Write the updated contents back to the CSV file
                try (PrintWriter pw = new PrintWriter(new FileWriter("inventory_data.csv"))) {
                    for (String line : lines) {
                        pw.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                loadData();

                Alert alert1 = new Alert(AlertType.INFORMATION);
                alert1.setTitle("Success!");
                alert1.setHeaderText("Successfully deleted product and dissociated the parts!");
                alert1.setContentText(null);
                alert1.showAndWait();
            }
        } else {
            // Showing no product selected dialog box
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error!");
            alert.setHeaderText("Please, Select at-least one product to perform delete operation!");
            alert.setContentText(null);
            alert.showAndWait();
        }

    }


    /**
     * Performs search operations on the products table
     */
    @FXML
    void productSearch() {

        if (!productFilterString.getText().isEmpty()) {
            ObservableList<Product> filteredData;
            filteredData = Inventory.lookupProduct(productFilterString.getText());
            // Wrap the FilteredList in a SortedList.
            SortedList<Product> sortedData1 = new SortedList<>(filteredData);
            // Bind the SortedList comparator to the TableView comparator.
            sortedData1.comparatorProperty().bind(productTable.comparatorProperty());
            // Add sorted (and filtered) data to the table.
            productTable.setItems(sortedData1);
        }
    }

    /**
     * Prepares the initial data for the system
     *
     * loads data from external source inventory_data.csv
     *
     * this will allow us to perform the CRUD concepts to the data
     */
    public void prepareData(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        //TODO check the modifications i made here
        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            if (values[0].equals("InHouse")) {
                InHouse part = new InHouse(Integer.parseInt(values[1]), values[2], Double.parseDouble(values[3]),
                        Integer.parseInt(values[4]), Integer.parseInt(values[5]), Integer.parseInt(values[6]),
                        Integer.parseInt(values[7]));
                Inventory.addPart(part);
            } else if (values[0].equals("Outsourced")) {
                Outsourced part = new Outsourced(Integer.parseInt(values[1]), values[2], Double.parseDouble(values[3]),
                        Integer.parseInt(values[4]), Integer.parseInt(values[5]), Integer.parseInt(values[6]), values[7]);
                Inventory.addPart(part);
            } else if (values[0].equals("Product")) {
                Product product = new Product(Integer.parseInt(values[1]), values[2], Double.parseDouble(values[3]),
                        Integer.parseInt(values[4]), Integer.parseInt(values[5]), Integer.parseInt(values[6]));
                Inventory.addProduct(product);
                for (int i = 7; i < values.length; i++) {
                    Part part = Inventory.lookupPart(Integer.parseInt(values[i]));
                    if (part != null) {
                        product.addAssociatedPart(part);
                    }
                }
            }
        }
        reader.close();
    }



    /**
     * Adds the new part into the parts lists
     *
     * this newly added data is added to the inventory_data.csv file
     */
    public void addNewPart(int pID, String pName, int pLevel, double pCost, int pMax, int pMin, String pCompMac, Boolean inHouse, int asID) throws IOException {
        //TODO check the modifications i made here
        Part newPart;
        //Check the type of parts and create its object appropriately
        //Write the new part to the CSV file
        FileWriter writer = new FileWriter("inventory_data.csv", true);
        String record = "";
        if (inHouse) {
            record = "InHouse," + pID + "," + pName + "," + pCost + "," + pLevel + "," + pMin + "," + pMax + "," + pCompMac;
        } else {
            record = "Outsourced," + pID + "," + pName + "," + pCost + "," + pLevel + "," + pMin + "," + pMax + "," + pCompMac;
        }
        writer.write(record + "\n");
        writer.close();

        loadData();
    }

    /**
     * Adds the new product to the product list
     *
     * this newly added data is added to the inventory_data.csv file
     * */
    public void addNewProduct(int pID, String pName, int pLevel, double pCost, int pMax, int pMin, ObservableList<Part> parts) throws IOException {
        // adds new products to arraylist

        //TODO check the modifications i made here
        Product product = new Product(pID, pName, pCost, pLevel, pMin, pMax);
        if (!parts.isEmpty()) {
            for (Part part : parts) {
                product.addAssociatedPart(part);
            }
        }
        Inventory.addProduct(product);

        //Write the new product to the CSV file
        FileWriter writer = new FileWriter("inventory_data.csv", true);
        String record = "Product," + pID + "," + pName + "," + pCost + "," + pLevel + "," + pMin + "," + pMax;
        if (!parts.isEmpty()) {
            for (Part part : parts) {
                record += "," + part.getId();
            }
        }
        writer.write(record + "\n");
        writer.close();

        loadData();
    }

    /**
     * Updates the parts details
     * this updated data is updated on the respective record in the inventory_data.csv file
     * */
    void updatePart(int pID, String pName, int pLevel, double pCost, Part selectedPart, int pMax, int pMin, String pCompMach, Boolean inHouse, int asID) {
        // setting the modified values
        //TODO check the modifications i made here
        Part updatedPart;
        if (inHouse) {
            InHouse _selectedPart = (InHouse) selectedPart;
            _selectedPart.setName(pName);
            _selectedPart.setStock(pLevel);
            _selectedPart.setPrice(pCost);
            _selectedPart.setMax(pMax);
            _selectedPart.setMin(pMin);
            _selectedPart.setMachineId(Integer.parseInt(pCompMach));
            updatedPart = _selectedPart;
        } else {
            Outsourced _selectedPart = (Outsourced) selectedPart;
            _selectedPart.setName(pName);
            _selectedPart.setStock(pLevel);
            _selectedPart.setPrice(pCost);
            _selectedPart.setMax(pMax);
            _selectedPart.setMin(pMin);
            _selectedPart.setCompanyName(pCompMach);
            updatedPart = _selectedPart;
        }

        // updating the part in the inventory list
        for (int i = 0; i < Inventory.getAllParts().size(); i++) {
            if (Inventory.getAllParts().get(i).getId() == pID)
                Inventory.updatePart(i, updatedPart);
        }

        // updating the part in the file
        try {
            FileWriter writer = new FileWriter("inventory_data.csv");
            for (Part part : Inventory.getAllParts()) {
                if (part instanceof InHouse) {
                    InHouse inHousePart = (InHouse) part;
                    writer.write("InHouse," + inHousePart.getId() + "," + inHousePart.getName() + "," + inHousePart.getPrice() + "," + inHousePart.getStock() + "," + inHousePart.getMin() + "," + inHousePart.getMax() + "," + inHousePart.getMachineId() + "\n");
                } else {
                    Outsourced outsourcedPart = (Outsourced) part;
                    writer.write("Outsourced," + outsourcedPart.getId() + "," + outsourcedPart.getName() + "," + outsourcedPart.getPrice() + "," + outsourcedPart.getStock() + "," + outsourcedPart.getMin() + "," + outsourcedPart.getMax() + "," + outsourcedPart.getCompanyName() + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadData();
    }
 /**
     * Updates the product details
  *
  * this updated data is updated on the respective record in the inventory_data.csv file
     * */
 void updateProduct(int pID, String pName, int pLevel, double pCost, Product productSelected, int pMax, int pMin) {
     // setting the modified values

     //TODO check the modifications i made here
     productSelected.setName(pName);
     productSelected.setPrice(pCost);
     productSelected.setStock(pLevel);
     productSelected.setMax(pMax);
     productSelected.setMin(pMin);

     // create a new ArrayList to store the updated product information
     ArrayList<String> updatedLines = new ArrayList<>();

     try {
         // read the contents of the inventory_data.csv file and store each line as a String in a List
         List<String> lines = Files.readAllLines(Paths.get("inventory_data.csv"));

         // loop through the List and split each line into an array of values using a comma delimiter
         for (String line : lines) {
             String[] values = line.split(",");

             // if the first value in the array matches the selected product ID, update the values and add the updated line to the ArrayList
             if (Integer.parseInt(values[1]) == pID) {
                 values[2] = pName;
                 values[3] = Double.toString(pCost);
                 values[4] = Integer.toString(pLevel);
                 values[5] = Integer.toString(pMax);
                 values[6] = Integer.toString(pMin);
                 String updatedLine = String.join(",", values);
                 updatedLines.add(updatedLine);
             }
             // if the first value in the array does not match the selected product ID, add the original line to the ArrayList
             else {
                 updatedLines.add(line);
             }
         }

         // write the updated ArrayList to the inventory_data.csv file, overwriting the original contents
         Files.write(Paths.get("inventory_data.csv"), updatedLines, StandardCharsets.UTF_8);

     } catch (IOException e) {
         e.printStackTrace();
     }

     loadData();
 }

    /**
     * Checks if the part is associated to any of the available products
     * */
    private Product isAssociated(Part part) {
        for (Product product : Inventory.getAllProducts()) {
            if (product.getAllAssociatedParts().contains(part))
                return product;
        }
        return null;
    }

}
