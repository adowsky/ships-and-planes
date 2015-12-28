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
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import world.PassengerGenerator;
import world.vehicles.SerializationParser;
import world.SerializeContainer;
import world.ports.*;
import world.vehicles.*;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * FXML Controller for application's window.
 */

public class FXMLWindowController implements Initializable,Serializable {
    private static FXMLWindowController instance;

    private final String SAVE_FILE = "map.save";
    @FXML private Canvas canvas;
    @FXML private BorderPane controlPanel;
    @FXML private Pane mapPane;

    private Object enablingProtector = new Object();
    private Parent civilianPlane = null;
    private Parent civilianAirForm = null;
    private Parent militaryAirForm = null;
    private VehicleDetails vehicleDetails;
    private EventHandler<ActionEvent> civilianShipClicked;
    private EventHandler<ActionEvent> militaryShipClicked;
    private EventHandler<ActionEvent> militaryPlaneClicked;
    private EventHandler<ActionEvent> civilianPlaneClicked;
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
                vehicleDetails.setDetails((VehicleButton) event.getSource());
                controlPanel.setCenter(vehicleDetails);
                allEnabled();
                setChoosingState(false);
            });

        };
        militaryShipClicked = event -> {
            ObservableList list = vehicleDetails.getChildren();
            VehicleButton source =(VehicleButton)event.getSource();
            fromSea =source;
            Platform.runLater(()->{
                AircraftFormController.getInstance().setPortName(source.getModel().getClass());
                AircraftFormController.getInstance().setFromSea((AircraftCarrier)source.getModel());
                AircraftFormController.getInstance().lockArmamentType(((AircraftCarrier)source.getModel()).getArmament());
                list.clear();
                Map<String, String> propertiesMap = source.getModel().getProperties();
                int i =propertiesMap.size()-1;
                for(String s : propertiesMap.keySet()){
                    vehicleDetails.add(new Label(s+": "+propertiesMap.get(s)),0,i);
                    i--;
                }
                VBox box = new VBox();
                box.getChildren().addAll(vehicleDetails,militaryAirForm);
                controlPanel.setCenter(box);
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
        vhc = new VehicleButton();
        AircraftCarrier carrier = new AircraftCarrier(reykjavik,0.02,ArmamentType.NUCLEAR_WEAPON);
        vhc.setModel(carrier);
        vhc.getStyleClass().add("civilian-ship");
        vhc.setOnAction(militaryShipClicked);
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
     private   VehicleButton btn;
     public void setDetails(VehicleButton btn){
         Vehicle v = btn.getModel();
         this.btn = btn;
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
             mapPane.getChildren().remove(btn);
             btn.destroy();
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
    @FXML public void loadFromFile(){
        try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(SAVE_FILE)))){
            Set<Serializable> serializables = (Set<Serializable>)in.readObject();
            List<Notifiable> list = (List<Notifiable>)in.readObject();
            SerializationParser parser = new SerializationParser(serializables);
            parser.parse();
            mapPane.getChildren().clear();
            mapPane.getChildren().add(canvas);
            final GraphicsContext gc = canvas.getGraphicsContext2D();
            Image map = new Image(getClass().getResourceAsStream("resources/blank-world-map (1).jpg"));
            gc.drawImage(map,0,0);
            mapPane.getChildren().addAll(finishParsingPorts(parser.getPortButtonsWithName()));
            mapPane.getChildren().addAll(finishParsingVehicles(parser.getVehicleButtonsWithName()));
            SynchronizedUpdateNotifier.INSTANCE.addNewList(list);
        }catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }
    private Set<PortButton> finishParsingPorts(Set<PortButton> list){
        for(PortButton p : list){
            if(p.getName().equals("harbour")){
                p.setOnAction(event -> seaPortClick(p));
            }else if(p.getName().equals("military airport")){
                p.setOnAction(event -> mAirPortClicked(p));
            }else if(p.getName().equals("civilian airport")){
                p.setOnAction(event -> airPortClicked(p));
            }
        }
        return  list;
    }
    private Set<VehicleButton> finishParsingVehicles(Set<VehicleButton> list){
        for(VehicleButton p : list){
            if(p.getModel().getClass().getSimpleName().equals("Airliner")){
                p.setOnAction(civilianShipClicked);
                p.getModel().setReadyToTravel();
            }else if(p.getModel().getClass().getSimpleName().equals("FerryBoat")){
                p.setOnAction(civilianShipClicked);
                p.getModel().setReadyToTravel();
            }else if(p.getModel().getClass().getSimpleName().equals("AircraftCarrier")){
                p.setOnAction(militaryShipClicked);
                p.getModel().setReadyToTravel();
            }else if(p.getModel().getClass().getSimpleName().equals("MilitaryAircraft")){
                p.setOnAction(civilianShipClicked);
                p.getModel().setReadyToTravel();
            }
        }
        return  list;
    }
    @FXML public void saveToFile(){
        try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(SAVE_FILE)))){

            out.writeObject(SerializeContainer.getInstance().getSerializables());
            out.writeObject(SynchronizedUpdateNotifier.INSTANCE.toSerialize());

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
