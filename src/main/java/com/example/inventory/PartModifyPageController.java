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

import java.net.URL;
import java.util.ResourceBundle;

public class PartModifyPageController implements Initializable {

    /*----------------------------------------All FXML Button, Field, RadioButton,Label Declaration-----------------------*/

    @FXML
    private TextField partCompanyNameField, partPriceField, partMaxField, partMinField, partNameTextField, partLnvField, partIDTextField, partMachineIDField;
    @FXML
    private RadioButton inHouseButton, outsourcedButton;
    @FXML
    private Label partCompanyNameLabel, partMachineIDLabel;

    /*----------------------------------------All FXML Button, Field, RadioButton,Label Declaration-----------------------*/

    // Parts Class object declaration for future use
    Part selectedPart;
    Boolean inHouse = true;
    // Parent class reference variable declaration
    private HomeController documentController;

    // ID of associated part, default -1 for individual parts
    int asProID = -1;

    /**
     * Closes the part modify form
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

        // On in-house button press show the machine id field and label visible
        partMachineIDField.setVisible(true);
        partMachineIDLabel.setVisible(true);

    }

    /**
     * Validates the input and updates the parts data into the system data storage
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
                        documentController.updatePart(
                                Integer.parseInt(partIDTextField.getText()), // Parsing String to Integer format to send as parameter
                                partNameTextField.getText(),
                                Integer.parseInt(partLnvField.getText()), // Parsing String to Integer format to send as parameter
                                Double.parseDouble(partPriceField.getText()), // Parsing String to Double format to send as parameter
                                selectedPart, // Referencing Selected part to modify data of
                                Integer.parseInt(partMaxField.getText()), // Parsing max value from text field to integer
                                Integer.parseInt(partMinField.getText()), // Parsing min value from text field to integer
                                companyNameOrMachineID,
                                inHouse,
                                asProID
                        );

                        // Show data update successful dialog
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success!");
                        alert.setHeaderText("Successfully Updated Part Data!");
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
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Initializes the parent controller
     */
    void setParentController(HomeController documentController) {
        this.documentController = documentController;
    }

    /**
     * Populates the form with the details of the selected part
     */
    void setData(Part selectedPart) {
        if (selectedPart != null) {
            this.selectedPart = selectedPart;
            partIDTextField.setText(String.valueOf(selectedPart.getId()));      // Setting value of part id to the Modify window
            partNameTextField.setText("" + selectedPart.getName());                 // Setting value of part name to the Modify window
            partLnvField.setText("" + selectedPart.getStock());                  // Setting value of part lnv to the Modify window
            partPriceField.setText("" + selectedPart.getPrice());               // Setting value of part price to the Modify window
            partMaxField.setText("" + selectedPart.getMax());
            partMinField.setText("" + selectedPart.getMin());
            if (selectedPart instanceof InHouse) {
                inHouseButtonAction(null);
                partMachineIDField.setText("" + ((InHouse) selectedPart).getMachineId());      // setting machine id for in house parts
            } else {
                outsourceButtonPress(null);
                partCompanyNameField.setText("" + ((Outsourced) selectedPart).getCompanyName());        // setting company name for outsourced parts
            }
        }

    }
}
