package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import world.Passenger;
import world.WorldConstants;
import world.vehicles.Vehicle;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller of form to create airliners.
 */
public class AirlinerFormController implements ChoosingController, Initializable, Serializable{
    private static AirlinerFormController instance;
    private final String ERROR_MSG = "Error occurred! Check all form fields and try again!";
    private final String SUCCESS_MSG = "Airliner has been created!";

    private boolean choosing = false;
    @FXML
    private TextField maxFuel;
    @FXML private TextField staffAmount;
    @FXML private TextField maxCapacity;
    @FXML private Button routeButton;
    @FXML private Label info;
    @FXML private TextField routeText;
    @FXML private Label portName;
    @FXML private ListView<Passenger> passengerListView;
    @FXML private ListView<Vehicle> vehicleView;
    private String activeName;

    public static AirlinerFormController getInstance(){
        return instance;
    }

    @FXML public void chooseRoute(ActionEvent event){
        if(!choosing) {
            enableChoosing();
        }else{
            disableChoosing();
        }

    }
    @FXML public void removeDockedVehicle(){
        if(vehicleView.getSelectionModel().getSelectedItem() == null)
            return;
        vehicleView.getSelectionModel().getSelectedItem().destroy();

    }
    public void setPortName(String name){
        portName.setText(name);
        routeText.clear();
        routeText.appendText(name.split(":")[1].trim());
    }
    public void enableChoosing(){
        choosing = true;
        FXMLWindowController.getInstance().onlyCivilianPlanesEnabled();
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
        maxFuel.clear();
        maxCapacity.clear();
//        speed.clear();
        routeText.clear();
    }
    @FXML public void addNewVehicle(ActionEvent event){
        disableChoosing();
        if(validate()){
            FXMLWindowController.getInstance().addVehicleButton(mapShipDetails());
            clearForm();
            info.setText(SUCCESS_MSG);
            info.setTextFill(Color.GREEN);
        }else {
            info.setText(ERROR_MSG);
            info.setTextFill(Color.RED);
        }

    }
    private boolean validate(){
        if(staffAmount.getText().equals(""))
            return false;
        if(maxFuel.getText().equals(""))
            return false;
        if(maxCapacity.getText().equals(""))
            return false;
        if(routeText.getText().equals(""))
            return false;
        return true;
    }
    private Map<String, Object[]> mapShipDetails(){
        Map<String, Object[]> shipDetails = new HashMap<>();
        shipDetails.put("Max fuel", new String[]{maxFuel.getText()});
        shipDetails.put("Staff amount",new String[]{staffAmount.getText()});
        shipDetails.put("Max capacity", new String[]{maxCapacity.getText()});
        shipDetails.put("Speed", new String[]{String.valueOf(WorldConstants.AIRPLANE_SPEED)});
        shipDetails.put("Route", routeText.getText().trim().split("-"));
        shipDetails.put("Type", new String[]{"Airliner"});
        return shipDetails;
    }

    @Override
    public void ChoiceHasBeenMade(String item) {
        routeText.appendText("-"+item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronized (AirlinerFormController.class){
            if(instance == null)
                instance = this;
        }
        vehicleView.setItems(FXCollections.observableArrayList());
        passengerListView.setItems(FXCollections.observableArrayList());
    }
    @FXML
    public void showPassenger(){
        Passenger p = passengerListView.getSelectionModel().getSelectedItem();
        if(p == null)
            return;
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Passenger details");
        alert.setHeaderText(p.getFirstname()+" "+p.getLastname());
        GridPane gp = new GridPane();
        gp.add(new Label("PESEL: "+p.getPesel()),0,0);
        gp.add(new Label("Age: "+p.getAge()),0 ,1);
        alert.getDialogPane().setContent(gp);
        alert.showAndWait();
    }
    public void fillPassengers(Collection<Passenger> col){
        passengerListView.getItems().clear();
        passengerListView.getItems().addAll(col);
    }
    public void fillVehicles(Collection<? extends Vehicle> col){
        vehicleView.getItems().clear();
        vehicleView.getItems().addAll(col);
    }
}
