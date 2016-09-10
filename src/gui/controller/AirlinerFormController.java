package gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import world.WorldConstants;
import world.passenger.Passenger;
import world.ports.air.CivilianAirport;
import world.vehicles.Vehicle;

import java.net.URL;
import java.util.*;

/**
 * Controller of form to create airliners.
 */
public class AirlinerFormController extends FormController {
    private static AirlinerFormController instance;

    private Random rand;
    @FXML
    private TextField maxFuel;
    @FXML
    private TextField staffAmount;
    @FXML
    private TextField maxCapacity;
    @FXML
    private TextField routeText;
    @FXML
    private Label portName;
    @FXML
    private ListView<Passenger> passengerListView;
    @FXML
    private ListView<Vehicle> vehicleView;

    public static AirlinerFormController getInstance() {
        return instance;
    }

    @FXML
    public void removeDockedVehicle() {
        if (vehicleView.getSelectionModel().getSelectedItem() == null)
            return;
        vehicleView.getSelectionModel().getSelectedItem().destroy();

    }

    public void setPortName(String name) {
        portName.setText(name);
        routeText.clear();
        routeText.appendText(name.split(":")[1].trim());
    }

    @Override
    public void enableChoosing() {
        super.enableChoosing();
        FXMLWindowController.getInstance().onlyCivilianPlanesEnabled();
        FXMLWindowController.getInstance().setChoosingState(true);
        FXMLWindowController.getInstance().setChoosingTarget(this);
        routeButton.setText("Stop");
    }

    @Override
    protected void clearForm() {
        staffAmount.clear();
        maxFuel.clear();
        maxCapacity.clear();
        routeText.clear();
    }

    @Override
    protected boolean validate() {
        if (staffAmount.getText().equals(""))
            return false;
        if (maxFuel.getText().equals(""))
            return false;
        if (maxCapacity.getText().equals(""))
            return false;
        if (routeText.getText().split("-").length <= 1)
            return false;
        return true;
    }

    @Override
    protected Map<String, Object[]> mapShipDetails() {
        Map<String, Object[]> shipDetails = new HashMap<>();
        shipDetails.put("Max fuel", new String[]{maxFuel.getText()});
        shipDetails.put("Staff amount", new String[]{staffAmount.getText()});
        shipDetails.put("Max capacity", new String[]{maxCapacity.getText()});
        shipDetails.put("Speed", new String[]{String.valueOf(WorldConstants.AIRPLANE_SPEED)});
        shipDetails.put("Route", routeText.getText().trim().split("-"));
        shipDetails.put("Type", new String[]{"Airliner"});
        return shipDetails;
    }

    @Override
    public void choiceHasBeenMade(String item) {
        String[] arr = routeText.getText().split("-");
        if (!arr[arr.length - 1].equals(item))
            routeText.appendText("-" + item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronized (AirlinerFormController.class) {
            if (instance == null)
                instance = this;
        }
        vehicleView.setItems(FXCollections.observableArrayList());
        passengerListView.setItems(FXCollections.observableArrayList());
        rand = new Random();
    }

    @FXML
    public void showPassenger() {
        Passenger p = passengerListView.getSelectionModel().getSelectedItem();
        if (p == null)
            return;
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Passenger details");
        alert.setHeaderText(p.getFirstname() + " " + p.getLastname());
        GridPane gp = new GridPane();
        gp.add(new Label("PESEL: " + p.getPesel()), 0, 0);
        gp.add(new Label("Age: " + p.getAge()), 0, 1);
        StringJoiner route = new StringJoiner("-");
        p.getJourney().getRoute().forEach(e -> route.add(e.toString().split(":")[1].trim()));
        gp.add(new Label("Route: " + route.toString()), 0, 2);
        alert.getDialogPane().setContent(gp);
        alert.showAndWait();
    }

    public void fillPassengers(Collection<Passenger> col) {
        passengerListView.getItems().clear();
        passengerListView.getItems().addAll(col);
    }

    public void fillVehicles(Collection<? extends Vehicle> col) {
        vehicleView.getItems().clear();
        vehicleView.getItems().addAll(col);
    }

    public void clearInformationText() {
        info.setText("");
    }

    public void randomFill(List<CivilianAirport> list, CivilianAirport mair) {
        staffAmount.setText(String.valueOf(rand.nextInt(51)));
        maxFuel.setText(String.valueOf(rand.nextInt(((int) WorldConstants.MAX_FUEL_VALUE) * 4 + 1000)));
        maxCapacity.setText(String.valueOf(rand.nextInt(50) + 50));
        List<CivilianAirport> ports = randomPorts(mair, list);

        ports.forEach(e -> choiceHasBeenMade(e.getName()));
    }
}
