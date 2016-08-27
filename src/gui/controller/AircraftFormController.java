package gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import world.WorldConstants;
import world.ports.air.MilitaryAirport;
import world.vehicles.types.water.AircraftCarrier;
import world.vehicles.ArmamentType;
import world.vehicles.Vehicle;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * Controller of form to create new Aircraft.
 */
public class AircraftFormController implements ChoosingController, Initializable,Serializable {

    private static AircraftFormController instance;
    private final String ERROR_MSG = "Error occurred! Check all form fields and try again!";
    private final String SUCCESS_MSG = "Aircraft has been created!";
    private AircraftCarrier fromSea = null;
    private boolean choosing = false;
    private Random rand;
    @FXML private TextField maxFuel;
    @FXML private TextField staffAmount;
    @FXML private Button routeButton;
    @FXML private Label info;
    @FXML private TextField routeText;
    @FXML private Label portName;
    @FXML private ComboBox armType;
    @FXML private ListView<Vehicle> vehicleView;

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
        if(vehicleView.getSelectionModel().getSelectedItem() == null)
            return;
        vehicleView.getSelectionModel().getSelectedItem().destroy();

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
    public void setPortName(Class<?> cls){
        portName.setText(cls.getSimpleName());
        routeText.clear();
    }

    public void setFromSea(AircraftCarrier fromSea) {
        this.fromSea = fromSea;
    }
    public void lockArmamentType(ArmamentType arm){
        armType.getSelectionModel().select(arm);
        armType.setDisable(true);
    }
    public void unlockArmamentType(){
        armType.setDisable(false);
    }

    /**
     * Enables choosing.
     */
    public void enableChoosing(){
        choosing = true;
        FXMLWindowController.getInstance().onlyMilitaryPlanesEnabled();
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
        if(fromSea != null) {
            FXMLWindowController.getInstance().addVehicleButton(mapShipDetails(), false);
        }

        else
            FXMLWindowController.getInstance().addVehicleButton(mapShipDetails(),true);
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
        if(routeText.getText().split("-").length <= 1)
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
        if(fromSea != null)
            shipDetails.put("Start", new String[]{"false"});
        else
            shipDetails.put("Start", new String[]{"true"});
        return shipDetails;
    }

    @Override
    public void ChoiceHasBeenMade(String item) {
        String[] arr = routeText.getText().split("-");
        if(!arr[arr.length - 1].equals(item))
        if(routeText.getText().isEmpty())
            routeText.appendText(item);
        else
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
        rand = new Random();
    }
    public void fillVehicles(Collection<? extends Vehicle> col){
        vehicleView.getItems().clear();
        vehicleView.getItems().addAll(col);
    }
    public void clearInformationText(){
        info.setText("");
    }
    public void randomFill(List<MilitaryAirport> list, MilitaryAirport mair){
        staffAmount.setText(String.valueOf(rand.nextInt(51)));
        maxFuel.setText(String.valueOf(rand.nextInt(((int)WorldConstants.MAX_FUEL_VALUE)*4+1000)));
        armType.getSelectionModel().select(rand.nextInt(armType.getItems().size()));
        List<MilitaryAirport> ports = new ArrayList<>();
        if(list.size()<4) {
            list.forEach(e -> {
                if (e != mair)
                    ports.add(e);
            });
            Collections.shuffle(ports);
        }
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
    }
}
