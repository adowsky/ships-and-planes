package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import world.Journey;
import world.Passenger;
import world.vehicles.FerryBoat;
import world.vehicles.Vehicle;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for creating form of civilian ship.
 */
public class FerryFormController implements ChoosingController, Initializable, Serializable{
    private static FerryFormController instance;
    private final String ERROR_MSG = "Error occurred! Check all form fields and try again!";
    private final String SUCCESS_MSG = "Ferryboat has been created!";

    private boolean choosing = false;
    @FXML private TextField company;
    @FXML private TextField maxCapacity;
    @FXML private TextField speed;
    @FXML private Button routeButton;
    @FXML private Label info;
    @FXML private TextField routeText;
    @FXML private Label portName;
    @FXML private ListView<Passenger> passengerListView;
    @FXML private ListView<Vehicle> vehicleView;
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
    @FXML public void removeDockedVehicle(){
        if(vehicleView.getSelectionModel().getSelectedItem() == null)
            return;
        vehicleView.getSelectionModel().getSelectedItem().destroy();

    }
    public void setPortName(String name){
        portName.setText(name);
        routeText.clear();
        routeText.appendText(name.split(" ")[1]);
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
            info.setTextFill(Color.GREEN);
        }else {
            info.setText(ERROR_MSG);
            info.setTextFill(Color.RED);
        }

    }
    private boolean validate(){
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
    private Map<String, Object[]> mapShipDetails(){
        Map<String, Object[]> shipDetails = new HashMap<>();
        shipDetails.put("Company", new String[]{company.getText()});
        shipDetails.put("Max capacity", new String[]{maxCapacity.getText()});
        shipDetails.put("Speed", new String[]{speed.getText()});
        shipDetails.put("Route", routeText.getText().trim().split("-"));
        shipDetails.put("Type", new String[]{"Ferry"});
        return shipDetails;
    }
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

    @Override
    public void ChoiceHasBeenMade(String item) {
        routeText.appendText("-"+item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronized (FerryFormController.class){
            if(instance == null)
                instance = this;
        }
        passengerListView.setItems(FXCollections.observableArrayList());
        vehicleView.setItems(FXCollections.observableArrayList());
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
