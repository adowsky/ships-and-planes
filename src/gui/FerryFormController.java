package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import world.vehicles.FerryBoat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ado on 05.12.15.
 */
public class FerryFormController implements ChoosingController{
    private boolean choosing = false;
    @FXML private TextField staffAmount;
    @FXML private TextField maxFuel;
    @FXML private TextField maxCapacity;
    @FXML private TextField speed;
    @FXML private Button routeButton;
    @FXML private Label info;
    @FXML private TextField routeText;

    @FXML public void chooseRoute(ActionEvent event){
        if(!choosing) {
            enableChoosing();
        }else{
            disableChoosing();
        }

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
    @FXML public void addNewVehicle(ActionEvent event){
        disableChoosing();
        if(validate()){
            FXMLWindowController.getInstance().addVehicleButton(mapShipDetails());
        }

    }
    private boolean validate(){
        if(staffAmount.getText().equals(""))
            return false;
        if(maxFuel.getText().equals(""))
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
        shipDetails.put("Max fuel", new String[]{maxFuel.getText()});
        shipDetails.put("Max capacity", new String[]{maxCapacity.getText()});
        shipDetails.put("Speed", new String[]{speed.getText()});
        shipDetails.put("Route", routeText.getText().split(" "));
        shipDetails.put("Type", new String[]{"Ferry"});
        return shipDetails;
    }
}
