package com.example.inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


// It impliments initializables as default, it is kind of a start() method to initialize things at startup
public class PartPageController implements Initializable {

    /**
     * Initializes the controller class.
     */

    /*----------------------------------------All FXML Button, Field, RadioButton,Label Declaration-----------------------*/

    @FXML
    private TextField partCompanyNameField, partPriceField, partMaxField, partMinField, partNameTextField, partLnvField, partIDTextField, partMachineIDField;
    @FXML
    private RadioButton inHouseButton, outsourcedButton;
    @FXML
    private Label partCompanyNameLabel, partMachineIDLabel, formTitle;

    /*----------------------------------------All FXML Button, Field, RadioButton,Label Declaration-----------------------*/

    Boolean inHouse = true;
    // Parent class reference variable declaration 
    private HomeController documentController;

    // ID of associated part, default -1 for individual parts
    int asProID = -1;

    /**
     * Closes the add part form
     */
    @FXML
    void closeButtonAction(ActionEvent event) {

        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }

    /**
     * Checks the outsource radio button and changes the form to accept the input for Company name
     */
    @FXML
    void outsourceButtonPress(ActionEvent event) {

        // On outsourced button press hide the machine id label and field 
        partCompanyNameField.setVisible(true);
        partCompanyNameLabel.setVisible(true);

        inHouseButton.setSelected(false);
        inHouse = false;
        outsourcedButton.setSelected(true);

        // On outsourced button press make the company name field and label visible
        partMachineIDField.setVisible(false);
        partMachineIDLabel.setVisible(false);


    }

    /**
     * Checks the in-house radio button and changes the form to accept the input for Machine ID
     */
    @FXML
    void inHouseButtonAction(ActionEvent event) {

        // On inHouse button press hide the company name label and field 
        partCompanyNameField.setVisible(false);
        partCompanyNameLabel.setVisible(false);

        outsourcedButton.setSelected(false);
        inHouse = true;
        inHouseButton.setSelected(true);

        // On inhouse button press show the machine id field and label visible
        partMachineIDField.setVisible(true);
        partMachineIDLabel.setVisible(true);

    }

    /**
     * Validates the input and adds the parts data into the system data storage
     */
    @FXML
    void partSaveButtonAction(ActionEvent event) {
        try {
            if (!(partIDTextField.getText().isEmpty() || partNameTextField.getText().isEmpty() || partLnvField.getText().isEmpty()
                    || partPriceField.getText().isEmpty() || partMaxField.getText().isEmpty() || partMinField.getText().isEmpty())) {
                if (Integer.parseInt(partMaxField.getText()) > Integer.parseInt(partMinField.getText())) {
                    if (Integer.parseInt(partLnvField.getText()) < Integer.parseInt(partMaxField.getText())) {
                        String companyNameOrMachineID;
                        if (inHouse) {
                            companyNameOrMachineID = partMachineIDField.getText();
                        } else {
                            companyNameOrMachineID = partCompanyNameField.getText();
                        }
                        documentController.addNewPart(
                                Integer.parseInt(partIDTextField.getText()), // Parsing String to Integer format to send as parameter
                                partNameTextField.getText(),
                                Integer.parseInt(partLnvField.getText()), // Parsing String to Integer format to send as parameter
                                Double.parseDouble(partPriceField.getText()), // Parsing String to Double format to send as parameter
                                Integer.parseInt(partMaxField.getText()), // Parsing max value from text field to integer
                                Integer.parseInt(partMinField.getText()), // Parsing min value from text field to integer
                                companyNameOrMachineID,
                                inHouse,
                                asProID
                        );

                        // Show successfully new part addition dialog
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success!");
                        alert.setHeaderText("Successfully Added new Part!");
                        alert.setContentText(null);
                        alert.showAndWait();


                        // Closing the window after the save has been successfully finished
                        final Node source = (Node) event.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();

                    } else {
                        // Displays message when Max value can not be lower then inventory level value
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error!");
                        alert.setHeaderText("Max value can not be lower than inventory level value!");
                        alert.setContentText(null);
                        alert.showAndWait();
                    }
                } else {
                    // Displays message when Max value can not be lower then min value
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
            alert.setHeaderText("Invalid input. Lnv, Price, Min and Max as well as Machine ID should be number. Kindly check and try again");
            alert.setContentText(null);
            alert.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    /**
     * Default parent controller class setter method
     */
    void setParentController(HomeController documentController) {
        this.documentController = documentController;
    }

    /**
     * setting auto generated id for new part
     */
    void setID(int generateID) {
        partIDTextField.setText(String.valueOf(generateID));
    }

}
