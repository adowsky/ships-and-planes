package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import world.vehicles.FerryBoat;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for creating form of civilian ship.
 */
public class FerryFormController implements ChoosingController, Initializable{
    private static FerryFormController instance;
    private final String ERROR_MSG = "Error occurred! Check all form fields and try again!";
    private final String SUCCESS_MSG = "Ferryboat has been created!";

    private boolean choosing = false;
    @FXML private TextField staffAmount;
    @FXML private TextField company;
    @FXML private TextField maxCapacity;
    @FXML private TextField speed;
    @FXML private Button routeButton;
    @FXML private Label info;
    @FXML private TextField routeText;
    @FXML private Label portName;

    public static FerryFormController getInstance(){
        return instance;
    }

    @FXML public void chooseRoute(ActionEvent event){
        if(!choosing) {
            enableChoosing();
        }else{
            disableChoosing();
        }

    }
    public void setPortName(String name){
        portName.setText(name);
    }
    public void enableChoosing(){
        choosing = true;
        FXMLWindowController.getInstance().onlyCivilianShipsEnabled();
        FXMLWindowController.getInstance().setChoosingState(true);
        FXMLWindowController.getInstance().setChoosingTarget(this);
        routeButton.setText("Stop");
    }
    public void disableChoosing(){
        choosing = false;
        FXMLWindowController.getInstance().allEnabled();
        FXMLWindowController.getInstance().setChoosingState(false);
        routeButton.setText("Choose Route");
    }
    private void clearForm(){
        staffAmount.clear();
        company.clear();
        maxCapacity.clear();
        speed.clear();
        routeText.clear();
    }
    @FXML public void addNewVehicle(ActionEvent event){
        disableChoosing();
        if(validate()){
            FXMLWindowController.getInstance().addVehicleButton(mapShipDetails());
            clearForm();
            info.setText(SUCCESS_MSG);
        }else
            info.setText(ERROR_MSG);

    }
    private boolean validate(){
        if(staffAmount.getText().equals(""))
            return false;
        if(company.getText().equals(""))
            return false;
        if(maxCapacity.getText().equals(""))
            return false;
        if(speed.getText().equals(""))
            return false;
        if(routeText.getText().equals(""))
            return false;
        return true;
    }
    private Map<String, String[]> mapShipDetails(){
        Map<String, String[]> shipDetails = new HashMap<>();
        shipDetails.put("Staff amount", new String[]{staffAmount.getText()});
        shipDetails.put("Company", new String[]{company.getText()});
        shipDetails.put("Max capacity", new String[]{maxCapacity.getText()});
        shipDetails.put("Speed", new String[]{speed.getText()});
        shipDetails.put("Route", routeText.getText().trim().split(" "));
        shipDetails.put("Type", new String[]{"Ferry"});
        return shipDetails;
    }

    @Override
    public void ChoiceHasBeenMade(String item) {
        routeText.appendText(" "+item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronized (FerryFormController.class){
            if(instance == null)
                instance = this;
        }
    }
}
