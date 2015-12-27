package gui;

import exceptions.gui.MapInitException;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import world.PassengerGenerator;
import world.ports.*;
import world.vehicles.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * FXML Controller for application's window.
 */

public class FXMLWindowController implements Initializable {
    private static FXMLWindowController instance;

    @FXML private Canvas canvas;
    @FXML private BorderPane controlPanel;
    @FXML private Pane mapPane;

    private Object enablingProtector = new Object();
    private Parent civilianPlane = null;
    private Parent civilianAirForm = null;
    private Parent militaryAirForm = null;
    private VehicleDetails vehicleDetails;
    private EventHandler<ActionEvent> civilianShipClicked;
    private ChoosingController choosingTarget;
    private boolean choosingState = false;
    private VehicleButton fromSea = null;
    private MapInitializer initializer;
    private Set<PortButton<Harbour>> harbourButtons;
    private Set<PortButton<CivilianAirport>> CAirportButtons;
    private Set<PortButton<MilitaryAirport>> MAirportButtons;
    private PassengerGenerator passengerGenerator;

    /**
     * Returns instance of class.
     * @return instance of class.
     */
    public static FXMLWindowController getInstance(){
        synchronized(FXMLWindowController.class) {
            if (instance == null){
                instance = new FXMLWindowController();
            }
            return instance;
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronized (FXMLWindowController.class){
            instance = this;
        }
        passengerGenerator = PassengerGenerator.getInstance();
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        Image map = new Image(getClass().getResourceAsStream("resources/blank-world-map (1).jpg"));
        System.out.println("Initialization");
        gc.drawImage(map,0,0);


        vehicleDetails = new VehicleDetails();
        civilianShipClicked = event -> {
            Platform.runLater(()-> {
                vehicleDetails.setDetails(((VehicleButton) event.getSource()).getModel());
                controlPanel.setCenter(vehicleDetails);
                allEnabled();
                setChoosingState(false);
            });

        };

        Map<String, Harbour> ports= null;
        try{
            initializer = new MapInitializer(getClass().getResource("fxmls/map.xml").getPath());
            harbourButtons = initializer.getHarbours();
            CAirportButtons = initializer.getCivilianAirports();
            MAirportButtons = initializer.getMilitaryAirports();

            harbourButtons.forEach((btn) ->{btn.getStyleClass().add("seaport-button");
            btn.setOnAction(event -> seaPortClick(btn));});
            CAirportButtons.forEach((btn)->{
                btn.getStyleClass().add("airport-button");
                btn.setOnAction(event -> airPortClicked(btn));
            });
            MAirportButtons.forEach((btn)->{
                btn.getStyleClass().add("military-airport-button");
                btn.setOnAction(event -> mAirPortClicked(btn));
            });
            ports = initializer.getSeaPorts();
            mapPane.getChildren().addAll(harbourButtons);
            mapPane.getChildren().addAll(CAirportButtons);
            mapPane.getChildren().addAll(MAirportButtons);
            List<CivilianPort> db = new ArrayList<>();
            db.addAll(initializer.getCAirports().values());
            db.addAll(initializer.getSeaPorts().values());
            AircraftCarrier.setFlightRoutes(initializer.getConnectionForAircraftCarrier());
            passengerGenerator.setPortDataBase(db);
        }catch (MapInitException ex){
            ex.printStackTrace();
            Platform.exit();
        }
        try {
            civilianPlane = FXMLLoader.load(getClass().getResource("fxmls/civilian_ship_form.fxml"));
            civilianAirForm = FXMLLoader.load(getClass().getResource("fxmls/civilian_plane_form.fxml"));
            militaryAirForm = FXMLLoader.load(getClass().getResource("fxmls/military_plane_form.fxml"));
        }catch (IOException ex){
            ex.printStackTrace();
        }
        VehicleButton vhc = new VehicleButton();
        Harbour reykjavik = ports.get("Reykjavik");
        Harbour salvador = ports.get("Salvador");
        passengerGenerator.getPassengers(50);
        /*
        FerryBoat boat = new FerryBoat(reykjavik.getLocation(),0.01,Arrays.asList(reykjavik,salvador),50,"KOMODO");
        vhc.setModel(boat);
        vhc.getStyleClass().add("civilian-ship");
        vhc.setOnAction(civilianShipClicked);
        boat.setRoute(reykjavik.getRouteToPort(salvador));
        boat.setReadyToTravel();
        mapPane.getChildren().add(vhc);
        vhc = new VehicleButton();
        boat = new FerryBoat(salvador.getLocation(),0.01,Arrays.asList(salvador,reykjavik),50,"KOMODO");
        vhc.setModel(boat);
        vhc.getStyleClass().add("civilian-ship");
        vhc.setOnAction(civilianShipClicked);
        boat.setRoute(salvador.getRouteToPort(reykjavik));
        boat.setReadyToTravel();
        mapPane.getChildren().add(vhc);
        vhc = new VehicleButton();*/
        AircraftCarrier carrier = new AircraftCarrier(reykjavik,0.02,ArmamentType.NUCLEAR_WEAPON);
        vhc.setModel(carrier);
        vhc.getStyleClass().add("civilian-ship");
        vhc.setOnAction((e)->militaryShipClicked(vhc));
        carrier.setReadyToTravel();
        mapPane.getChildren().add(vhc);

    }

    /**
     * Handler for button event.
     * @param source source
     */
    @FXML public synchronized void seaPortClick(PortButton source){
        if(choosingState){
            String name = source.getModel().getName();
            choosingTarget.ChoiceHasBeenMade(name);
            return;
        }
        FerryFormController.getInstance().setPortName(source.getModel().toString());
        Platform.runLater(()->controlPanel.setCenter(civilianPlane));

    }
    @FXML public synchronized void militaryShipClicked(VehicleButton source){

        ObservableList list = vehicleDetails.getChildren();
        fromSea = source;
        Platform.runLater(()->{
            AircraftFormController.getInstance().setPortName(source.getModel().getClass());
            AircraftFormController.getInstance().setFromSea((AircraftCarrier)source.getModel());
            AircraftFormController.getInstance().lockArmamentType(((AircraftCarrier)source.getModel()).getArmament());
            list.clear();
            Map<String, String> map = source.getModel().getProperties();
            int i =map.size()-1;
            for(String s : map.keySet()){
                vehicleDetails.add(new Label(s+": "+map.get(s)),0,i);
                i--;
            }
            controlPanel.setTop(vehicleDetails);
            controlPanel.setCenter(militaryAirForm);});
    }
    /**
     * Handler for button event.
     * @param source source
     */
    @FXML public synchronized void airPortClicked(PortButton source){
        if(choosingState){
            String name = source.getModel().getName();
            choosingTarget.ChoiceHasBeenMade(name);
            return;
        }
        AirlinerFormController.getInstance().setPortName(source.getModel().toString());
        Platform.runLater(() -> controlPanel.setCenter(civilianAirForm));
    }
    /**
     * Handler for button event.
     * @param source source
     */
    @FXML public synchronized  void mAirPortClicked(PortButton source){
        if(choosingState){
            String name = source.getModel().getName();
            choosingTarget.ChoiceHasBeenMade(name);
            return;
        }
        AircraftFormController.getInstance().setPortName(source.getModel().toString());
        AircraftFormController.getInstance().setFromSea(null);
        AircraftFormController.getInstance().unlockArmamentType();
        Platform.runLater(() -> controlPanel.setCenter(militaryAirForm));
    }

    /**
     * Panel with details of vehicle.
     */
    class VehicleDetails extends GridPane{
     private   Vehicle v;
     public void setDetails(Vehicle v){

         this.v=v;
         this.getChildren().clear();
         Map<String, String> map = v.getProperties();
         int i =0;
         for(String key : map.keySet()){
             add(new Text(key),0,i);
             add(new Label(map.get(key)),1,i);
             i++;
         }
         if(v instanceof Airplane) {
             Button landing = new Button("Emergency Landing");
             landing.setOnAction((event) -> {
                 //TODO
                 System.out.println("Emergency Landing");
             });
             add(landing,0,i++);
         }
         Button destroy = new Button("Destroy Vehicle");
         destroy.setOnAction((event) ->{
             //TODO
             System.out.println("Destroying vehicle");
         });
         add(destroy,0,i++);

     }
    }

    /**
     * Disables all ports excepts harbours.
     */
    public void onlyCivilianShipsEnabled(){
        synchronized (enablingProtector) {
            for(PortButton port : CAirportButtons){
                port.setDisable(true);
            }
        }

    }

    /**
     * Disables all ports excepts civilian airport.
     */
    public void onlyCivilianPlanesEnabled(){
        for(PortButton port : harbourButtons){
            port.setDisable(true);
        }
    }

    /**
     * Enables all ports.
     */
    public void allEnabled(){
        for(PortButton<Harbour> port : harbourButtons){
            port.setDisable(false);
        }
        for(PortButton port : CAirportButtons){
            port.setDisable(false);
        }

    }

    /**
     * Sets choosing controller as target to send events about click.
     * @param o controller.
     */
    public void setChoosingTarget(ChoosingController o){
        choosingTarget = o;
    }

    /**
     * Sets state of choosing flag
     * @param state state.
     */
    public void setChoosingState(boolean state){
        choosingState = state;
    }

    /**
     * Adds new vehicle to the map
     * @param details map of vehicle details.
     */
    public void addVehicleButton(Map<String, Object[]> details){
        VehicleButton btn = parseDetails(details);
        mapPane.getChildren().add(btn);
        btn.getModel().setReadyToTravel();
    }
    public VehicleButton addVehicleButton(Map<String, Object[]> details,boolean startTravel){
        VehicleButton btn = parseDetails(details);
        mapPane.getChildren().add(btn);
        if(startTravel)
            btn.getModel().setReadyToTravel();
        else {
            btn.setVisible(false);
        }
        return btn;
    }

    /**
     * Parses details and creates vehicle button from them.
     * @param details details of new vehicle
     * @return new vehicle button
     */
    private VehicleButton parseDetails(Map<String, Object[]> details){

        int speed = Integer.valueOf((String)details.get("Speed")[0]);
        String type = (String)details.get("Type")[0];
        String[] route = (String[])details.get("Route");
        int maxCapacity = 0;
        if(details.get("Max capacity") != null)
            maxCapacity = Integer.valueOf((String)details.get("Max capacity")[0]);
        int staffAmount = 0;
        if(details.get("Staff amount") != null)
            staffAmount = Integer.valueOf((String)details.get("Staff amount")[0]);
        int maxFuel = 0;
        if(details.get("Max fuel") != null)
            maxFuel = Integer.valueOf((String)details.get("Max fuel")[0]);
        ArmamentType armType = null;
        if(details.get("Armament") != null)
            armType = (ArmamentType)details.get("Armament")[0];
        boolean start = false;
        if(details.get("Start") != null)
            start = Boolean.valueOf((String)details.get("Start")[0]);
        VehicleButton btn = new VehicleButton();
            //FACTORY
        if(type.equals("Ferry")){
            List<Harbour> portList = new ArrayList<>();
            Map<String, Harbour> ports = initializer.getSeaPorts();
            for(String s: route){
                portList.add(ports.get(s));
            }
            Point2D modelLocation = portList.get(0).getLocation();
            FerryBoat model = new FerryBoat(modelLocation, speed/100.0, portList, maxCapacity,(String)details.get("Company")[0]);
            btn.setModel(model);
            btn.getStyleClass().add("civilian-ship");
            passengerGenerator.getPassengers(model.getMaxPassengersAmount());
            btn.setOnAction(civilianShipClicked);
        }else if(type.equals("Airliner")){
            List<CivilianAirport> portList = new ArrayList<>();
            Map<String, CivilianAirport> ports = initializer.getCAirports();
            for(String s: route){
                portList.add(ports.get(s));
            }
            Point2D modelLocation = portList.get(0).getLocation();
            Airliner model = new Airliner(modelLocation, speed/100.0, portList, staffAmount, maxFuel, maxCapacity);
            btn.setModel(model);
            btn.getStyleClass().add("civilian-plane");
            passengerGenerator.getPassengers(model.getMaxPassengersAmount());
            btn.setOnAction(civilianShipClicked);
        }else if(type.equals("Aircraft")){
            List<MilitaryAirport> portList = new ArrayList<>();
            Map<String, MilitaryAirport> ports = initializer.getMAirPorts();
            for(String s: route){
                portList.add(ports.get(s));
            }
            Point2D modelLocation = portList.get(0).getLocation();
            MilitaryAircraft model = new MilitaryAircraft(modelLocation, speed/100.0, portList, staffAmount, maxFuel, armType,!start);
            if(!start)
                ((AircraftCarrier)fromSea.getModel()).addProducedPlane(model);
            btn.setModel(model);
            btn.getStyleClass().add("military-plane");
            btn.setOnAction(civilianShipClicked);
        }
        //end of factory

        return btn;
    }
}
