package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import world.WorldConstants;
import world.vehicles.ArmamentType;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller of form to create new Aircraft.
 */
public class AircraftFormController implements ChoosingController, Initializable {

    private static AircraftFormController instance;
    private final String ERROR_MSG = "Error occurred! Check all form fields and try again!";
    private final String SUCCESS_MSG = "Aircraft has been created!";

    private boolean choosing = false;
    @FXML private TextField maxFuel;
    @FXML private TextField staffAmount;
    @FXML private Button routeButton;
    @FXML private Label info;
    @FXML private TextField routeText;
    @FXML private Label portName;
    @FXML private ComboBox armType;

    /**
     * Returns instance of AircraftFormController
     * @return instance of AircraftFormController
     */
    public static AircraftFormController getInstance(){
        return instance;
    }

    /**
     * Handler for "Choose route" button event.
     * @param event
     */
    @FXML public void chooseRoute(ActionEvent event){
        if(!choosing) {
            enableChoosing();
        }else{
            disableChoosing();
        }

    }
    @FXML public void removeDockedVehicle(){

    }

    /**
     * Sets name of port
     * @param name name of port.
     */
    public void setPortName(String name){
        portName.setText(name);
        routeText.clear();
        routeText.appendText(name.split(":")[1].trim());
    }

    /**
     * Enables choosing.
     */
    public void enableChoosing(){
        choosing = true;
        FXMLWindowController.getInstance().onlyCivilianPlanesEnabled();
        FXMLWindowController.getInstance().setChoosingState(true);
        FXMLWindowController.getInstance().setChoosingTarget(this);
        routeButton.setText("Stop");
    }

    /**
     * Disables choosing.
     */
    public void disableChoosing(){
        choosing = false;
        FXMLWindowController.getInstance().allEnabled();
        FXMLWindowController.getInstance().setChoosingState(false);
        routeButton.setText("Choose Route");
    }

    /**
     * Clears form
     */
    private void clearForm(){
        staffAmount.clear();
        maxFuel.clear();
        routeText.clear();
    }

    /**
     * Handler for add button event.
     * @param event
     */
    @FXML public void addNewVehicle(ActionEvent event){
        disableChoosing();
        if(validate()){
            successAddition();
        }else {
            additionFailed();
        }

    }

    /**
     * Service addition and shows information about success.
     */
    private void successAddition(){
        FXMLWindowController.getInstance().addVehicleButton(mapShipDetails());
        clearForm();
        info.setText(SUCCESS_MSG);
        info.setTextFill(Color.GREEN);
    }

    /**
     * Shows information that addition failed.
     */
    private void additionFailed(){
        info.setText(ERROR_MSG);
        info.setTextFill(Color.RED);
    }

    /**
     * Validate form.
     * @return if form is valid.
     */
    private boolean validate(){
        if(staffAmount.getText().equals(""))
            return false;
        if(maxFuel.getText().equals(""))
            return false;
        if(armType.getSelectionModel().isEmpty())
            return false;
        if(routeText.getText().equals(""))
            return false;
        return true;
    }

    /**
     * Creates map with ship details form current form.
     * @return details of the ship to create.
     */
    private Map<String, Object[]> mapShipDetails(){
        Map<String, Object[]> shipDetails = new HashMap<>();
        shipDetails.put("Max fuel", new String[]{maxFuel.getText()});
        shipDetails.put("Staff amount",new String[]{staffAmount.getText()});
        Object arm = armType.getSelectionModel().getSelectedItem();
        shipDetails.put("Armament", new ArmamentType[]{((ArmamentType)armType.getSelectionModel().getSelectedItem())});
        shipDetails.put("Speed", new String[]{String.valueOf(WorldConstants.AIRPLANE_SPEED)});
        shipDetails.put("Route", routeText.getText().trim().split("-"));
        shipDetails.put("Type", new String[]{"Aircraft"});
        return shipDetails;
    }

    @Override
    public void ChoiceHasBeenMade(String item) {
        routeText.appendText("-"+item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronized (AircraftFormController.class){
            if(instance == null)
                instance = this;
        }
        ObservableList<ArmamentType> list = FXCollections.observableArrayList();
        for(ArmamentType ar : ArmamentType.values()){
            list.add(ar);
        }

        armType.setItems(list);
    }
}