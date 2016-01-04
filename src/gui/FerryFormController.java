package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import world.Passenger;
import world.ports.Harbour;
import world.vehicles.Vehicle;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * Controller for creating form of civilian ship.
 */
public class FerryFormController implements ChoosingController, Initializable, Serializable{
    private static FerryFormController instance;
    private final String ERROR_MSG = "Error occurred! Check all form fields and try again!";
    private final String SUCCESS_MSG = "Ferryboat has been created!";
    private Random rand;
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
        if(routeText.getText().split("-").length<= 1)
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
        StringJoiner route = new StringJoiner("-");
        p.getJourney().getRoute().forEach(e -> route.add(e.toString().split(":")[1].trim()));
        gp.add(new Label("Route: "+route.toString()),0, 2);
        alert.getDialogPane().setContent(gp);
        alert.showAndWait();
    }

    @Override
    public void ChoiceHasBeenMade(String item) {
        String[] arr = routeText.getText().split("-");
        if(!arr[arr.length - 1].equals(item))
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
        rand = new Random();
    }
    public void fillPassengers(Collection<Passenger> col){
        passengerListView.getItems().clear();
        passengerListView.getItems().addAll(col);
    }
    public void fillVehicles(Collection<? extends Vehicle> col){
        vehicleView.getItems().clear();
        vehicleView.getItems().addAll(col);
    }
    public void clearInformationText(){
        info.setText("");
    }
    public void randomFill(List<Harbour> list, Harbour mair){
        CompanyExamples[] cmpy = CompanyExamples.values();
        company.setText(cmpy[rand.nextInt(cmpy.length)].toString());
        maxCapacity.setText(String.valueOf(rand.nextInt(50)+50));
        speed.setText(String.valueOf(rand.nextInt(5)+1));
        List<Harbour> ports = new ArrayList<>();
        if(list.size()<4)
            list.forEach(e ->{
                if(e != mair)
                    ports.add(e);
            });
        else{
            for(int i=0;i<2;i++){
                boolean found = false;
                while(!found){
                    int index = rand.nextInt(list.size());
                    if(!ports.contains(list.get(index))){
                        found = true;
                        ports.add(list.get(index));
                    }

                }
            }
        }
        ports.forEach(e -> {
            ChoiceHasBeenMade(e.getName());
        });
        info.setText("");
    }
    private enum CompanyExamples{
        Riot, Comodo, AirLines, WoodoLines, TerroLines, KillerLines
    }
}
