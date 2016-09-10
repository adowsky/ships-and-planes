package gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import world.passenger.Passenger;
import world.ports.water.Harbour;
import world.vehicles.Vehicle;

import java.net.URL;
import java.util.*;

/**
 * Controller for creating form of civilian ship.
 */
public class FerryFormController extends FormController {
    private static FerryFormController instance;

    private Random rand;
    @FXML
    private TextField company;
    @FXML
    private TextField maxCapacity;
    @FXML
    private TextField speed;
    @FXML
    private TextField routeText;
    @FXML
    private Label portName;
    @FXML
    private ListView<Passenger> passengerListView;
    @FXML
    private ListView<Vehicle> vehicleView;

    public static FerryFormController getInstance() {
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
        routeText.appendText(name.split(" ")[1]);
    }

    @Override
    public void enableChoosing() {
        super.enableChoosing();
        FXMLWindowController.getInstance().onlyCivilianShipsEnabled();
        FXMLWindowController.getInstance().setChoosingState(true);
        FXMLWindowController.getInstance().setChoosingTarget(this);
        routeButton.setText("Stop");
    }


    protected void clearForm() {
        company.clear();
        maxCapacity.clear();
        speed.clear();
        routeText.clear();
    }

    protected boolean validate() {
        return !company.getText().equals("") &&
                !maxCapacity.getText().equals("") &&
                !speed.getText().equals("") &&
                routeText.getText().split("-").length > 1;
    }

    protected Map<String, Object[]> mapShipDetails() {
        Map<String, Object[]> shipDetails = new HashMap<>();

        shipDetails.put("Company", new String[]{company.getText()});
        shipDetails.put("Max capacity", new String[]{maxCapacity.getText()});
        shipDetails.put("Speed", new String[]{speed.getText()});
        shipDetails.put("Route", routeText.getText().trim().split("-"));
        shipDetails.put("Type", new String[]{"Ferry"});

        return shipDetails;
    }

    public void showPassenger() {
        Passenger p = passengerListView.getSelectionModel().getSelectedItem();
        if (p == null) {
            return;
        }

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

    @Override
    public void choiceHasBeenMade(String item) {
        String[] arr = routeText.getText().split("-");
        if (!arr[arr.length - 1].equals(item))
            routeText.appendText("-" + item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronized (FerryFormController.class) {
            if (instance == null)
                instance = this;
        }
        passengerListView.setItems(FXCollections.observableArrayList());
        vehicleView.setItems(FXCollections.observableArrayList());
        rand = new Random();
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

    public void randomFill(List<Harbour> list, Harbour mair) {
        CompanyExample[] examples = CompanyExample.values();
        company.setText(examples[rand.nextInt(examples.length)].toString());
        maxCapacity.setText(String.valueOf(rand.nextInt(50) + 50));
        speed.setText(String.valueOf(rand.nextInt(5) + 1));
        List<Harbour> ports = this.randomPorts(mair, list);
        ports.forEach(e -> choiceHasBeenMade(e.getName()));
        info.setText("");
    }

    private enum CompanyExample {
        Riot, Comodo, AirLines, WoodoLines, TerroLines, KillerLines
    }
}
