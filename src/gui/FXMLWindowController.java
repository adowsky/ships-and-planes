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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import world.Passenger;
import world.PassengerGenerator;
import world.SerializeContainer;
import world.ports.*;
import world.vehicles.*;
import world.vehicles.movement.Cross;
import world.vehicles.movement.RegisteringPortAdapter;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * FXML Controller for application's window.
 */

public class FXMLWindowController implements Initializable {
    private static FXMLWindowController instance;

    private final String SAVE_FILE = "map.save";
    @FXML private Canvas canvas;
    @FXML private BorderPane controlPanel;
    @FXML private Pane mapPane;

    private Parent civilianPlane = null;
    private Parent civilianAirForm = null;
    private Parent militaryAirForm = null;
    private VehicleDetails vehicleDetails;
    private EventHandler<ActionEvent> civilianShipClicked;
    private EventHandler<ActionEvent> militaryShipClicked;

    private ChoosingController choosingTarget;
    private boolean choosingState = false;
    private VehicleButton fromSea = null;
    private MapInitializer initializer;
    private Set<PortButton<Harbour>> harbourButtons;
    private Set<PortButton<CivilianAirport>> CAirportButtons;
    private Set<PortButton<MilitaryAirport>> MAirportButtons;
    private Map<String, Harbour> harbourMap;
    private Map<String, CivilianAirport> civilianAirportMap;
    private Map<String, MilitaryAirport> militaryAirportMap;
    private List<MilitaryAirport> militaryAirports;
    private List<CivilianAirport> civilianAirports;
    private List<Harbour> harbours;
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
        passengerGenerator = new PassengerGenerator();
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        Image map = new Image(getClass().getResourceAsStream("resources/blank-world-map (1).jpg"));
        System.out.println("Initialization");
        gc.drawImage(map,0,0);


        vehicleDetails = new VehicleDetails();
        civilianShipClicked = event -> {
            Platform.runLater(()-> {
                vehicleDetails.setDetails((VehicleButton) event.getSource());
                vehicleDetails.setEditableRoute(harbourMap);
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
                AircraftFormController.getInstance().randomFill(militaryAirports, null);
                AircraftFormController.getInstance().lockArmamentType(((AircraftCarrier)source.getModel()).getArmament());
                AircraftFormController.getInstance().fillVehicles(((AircraftCarrier)source.getModel()).getProducedPlanes());

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
            initializer = new MapInitializer(getClass().getResourceAsStream("fxmls/map.xml"));
            initializer.init();
            harbourButtons = initializer.getHarboursBtns();
            CAirportButtons = initializer.getcAirportBtns();
            MAirportButtons = initializer.getmAirportBtns();

            harbourMap = initializer.getSeaPorts();
            civilianAirportMap = initializer.getCAirports();
            militaryAirportMap = initializer.getMAirPorts();
            harbours = new ArrayList<>(harbourMap.values());
            civilianAirports = new ArrayList<>(civilianAirportMap.values());
            militaryAirports = new ArrayList<>(militaryAirportMap.values());

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
        AircraftCarrier carrier = new AircraftCarrier(reykjavik,0.02,ArmamentType.NUCLEAR_WEAPON);
        vhc.setModel(carrier);
        vhc.getStyleClass().add("military-ship");
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
        FerryFormController.getInstance().fillPassengers(source.getModel().getPassengers());
        FerryFormController.getInstance().fillVehicles(source.getModel().getVehicles());
        FerryFormController.getInstance().clearInformationText();
        FerryFormController.getInstance().randomFill(harbours, (Harbour)source.getModel());
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
        AirlinerFormController.getInstance().fillPassengers(source.getModel().getPassengers());
        AirlinerFormController.getInstance().fillVehicles(source.getModel().getVehicles());
        AirlinerFormController.getInstance().clearInformationText();
        AirlinerFormController.getInstance().randomFill(civilianAirports, (CivilianAirport)source.getModel());
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
        AircraftFormController.getInstance().fillVehicles(source.getModel().getVehicles());
        AircraftFormController.getInstance().setFromSea(null);
        AircraftFormController.getInstance().unlockArmamentType();
        AircraftFormController.getInstance().clearInformationText();
        AircraftFormController.getInstance().randomFill(militaryAirports, (MilitaryAirport)source.getModel());
        Platform.runLater(() -> controlPanel.setCenter(militaryAirForm));
    }



    /**
     * Disables all ports excepts harbours.
     */
    public void onlyCivilianShipsEnabled(){
            mapPane.getChildren().forEach(e -> e.setDisable(true));
            harbourButtons.forEach(e -> e.setDisable(false));
    }

    /**
     * Disables all ports excepts civilian airport.
     */
    public void onlyCivilianPlanesEnabled(){
        mapPane.getChildren().forEach(e -> e.setDisable(true));
        CAirportButtons.forEach(e -> e.setDisable(false));
    }

    /**
     * Disables everything excepts Military Airports.
     */
    public void onlyMilitaryPlanesEnabled(){
        mapPane.getChildren().forEach(e -> e.setDisable(true));
        MAirportButtons.forEach(e -> e.setDisable(false));
    }

    /**
     * Enables all ports.
     */
    public void allEnabled(){
        mapPane.getChildren().forEach(e -> e.setDisable(false));
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
        VehicleButton btn = parseDetails(details, true);
        mapPane.getChildren().add(btn);
        btn.getModel().setReadyToTravel();
    }

    /**
     * Adds new vehicle to the map
     * @param details map of vehicle details.
     * @param startTravel if tf vehicle is running after creation
     * @return new VehicleButton
     */
    public VehicleButton addVehicleButton(Map<String, Object[]> details,boolean startTravel){
        VehicleButton btn = parseDetails(details, startTravel);
        mapPane.getChildren().add(btn);
        if(!startTravel)
            btn.setVisible(false);

        return btn;
    }

    /**
     * Parses details and creates vehicle button from them.
     * @param details details of new vehicle
     * @return new vehicle button
     */
    private VehicleButton parseDetails(Map<String, Object[]> details, boolean startTravel){
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
            Map<String, Harbour> ports = harbourMap;
            for(String s: route){
                portList.add(ports.get(s));
            }
            Point2D modelLocation = portList.get(0).getLocation();
            FerryBoat model = new FerryBoat(modelLocation, speed/100.0, portList, maxCapacity,(String)details.get("Company")[0]);
            btn.setModel(model);
            btn.getStyleClass().add("civilian-ship");
            passengerGenerator.getPassengers(model.getMaxPassengersAmount());
            btn.setOnAction(civilianShipClicked);
            if(startTravel) {
                Thread tr = new Thread(() ->
                        ((Harbour) btn.getModel().getLastPort()).addNewlyProducedVehicle((FerryBoat) btn.getModel()));
                tr.setDaemon(true);
                tr.start();
            }
        }else if(type.equals("Airliner")){
            List<CivilianAirport> portList = new ArrayList<>();
            Map<String, CivilianAirport> ports = civilianAirportMap;
            for(String s: route){
                portList.add(ports.get(s));
            }
            Point2D modelLocation = portList.get(0).getLocation();
            Airliner model = new Airliner(modelLocation, speed/100.0, portList, staffAmount, maxFuel, maxCapacity);
            btn.setModel(model);
            btn.getStyleClass().add("civilian-plane");
            passengerGenerator.getPassengers(model.getMaxPassengersAmount());
            btn.setOnAction(civilianShipClicked);
            if(startTravel) {
                Thread tr = new Thread(() ->
                        ((CivilianAirport) btn.getModel().getLastPort()).addNewlyProducedVehicle((Airliner) btn.getModel()));
                tr.setDaemon(true);
                tr.start();
            }
        }else if(type.equals("Aircraft")){
            List<MilitaryAirport> portList = new ArrayList<>();
            Map<String, MilitaryAirport> ports = militaryAirportMap;
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
            if(startTravel) {
                Thread tr = new Thread(() ->
                        ((MilitaryAirport) btn.getModel().getLastPort()).addNewlyProducedVehicle((MilitaryAircraft) btn.getModel()));
                tr.setDaemon(true);
                tr.start();
            }
        }
        //end of factory
        return btn;
    }
    @FXML public void loadFromFile(){
        try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(SAVE_FILE)))){
            SynchronizedUpdateNotifier.INSTANCE.clear();

            Set<Serializable> serializables = (Set<Serializable>)in.readObject();
            harbourMap = (Map<String, Harbour>)in.readObject();
            civilianAirportMap = (Map<String, CivilianAirport>)in.readObject();
            militaryAirportMap = (Map<String, MilitaryAirport>)in.readObject();
            harbourButtons = (Set<PortButton<Harbour>>)in.readObject();
            CAirportButtons = (Set<PortButton<CivilianAirport>>)in.readObject();
            MAirportButtons = (Set<PortButton<MilitaryAirport>>)in.readObject();
            passengerGenerator = (PassengerGenerator)in.readObject();
            Map<Port, Map<Port,List<Cross>>>flightRoutes = (Map<Port, Map<Port,List<Cross>>>)in.readObject();
            AircraftCarrier.setFlightRoutes(flightRoutes);
            AircraftCarrier.setAdapters((Map<Port, RegisteringPortAdapter>)in.readObject() );


            harbours = new ArrayList<>(harbourMap.values());
            civilianAirports = new ArrayList<>(civilianAirportMap.values());
            militaryAirports = new ArrayList<>(militaryAirportMap.values());

            SerializationParser parser = new SerializationParser(serializables);
            SerializeContainer.getInstance().clear();
            parser.parse();
            mapPane.getChildren().forEach(e ->{
                if(e instanceof VehicleButton)
                    ((VehicleButton)e).getModel().destroy();
            });
            mapPane.getChildren().clear();
            mapPane.getChildren().add(canvas);
            final GraphicsContext gc = canvas.getGraphicsContext2D();
            Image map = new Image(getClass().getResourceAsStream("resources/blank-world-map (1).jpg"));
            gc.drawImage(map,0,0);
            Set<PortButton> pButtons = finishParsingPorts(parser.getPortButtonsWithName());
            mapPane.getChildren().addAll(pButtons);
            pButtons.forEach(e -> SerializeContainer.getInstance().addObjectToSerialize(e.getModel()));
            Set<VehicleButton> vBtns = finishParsingVehicles(parser.getVehicleButtonsWithName());
            mapPane.getChildren().addAll(vBtns);
            vBtns.forEach(e -> SerializeContainer.getInstance().addObjectToSerialize(e.getModel()));

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
                if(p.getModel().isReadyToTravel()) {
                    p.getModel().setReadyToTravel();
                }else{
                    p.setVisible(false);
                }
            }
        }
        return  list;
    }
    @FXML public void saveToFile(){
        try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(SAVE_FILE)))){
            out.writeObject(SerializeContainer.getInstance().getSerializables());
            out.writeObject(harbourMap);
            out.writeObject(civilianAirportMap);
            out.writeObject(militaryAirportMap);
            out.writeObject(harbourButtons);
            out.writeObject(CAirportButtons);
            out.writeObject(MAirportButtons);
            out.writeObject(passengerGenerator);
            out.writeObject(AircraftCarrier.getFlightRoutes());
            out.writeObject(AircraftCarrier.getAdapters());


        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Panel with details of vehicle.
     */
    class VehicleDetails extends GridPane implements ChoosingController{
        private VehicleButton btn;
        private Label field;
        private Button editor;
        private ListView<Passenger> passengers;
        private ListView<Vehicle> vehicles;
        private Button dstr;
        private Button show;
        private VBox vBox;
        private VBox pBox;
        private StringJoiner joiner;
        private Map<String , ? extends Port> portsSource;

        public VehicleDetails() {
            field = new Label();
            joiner = new StringJoiner("-");
            editor = new Button("Edit Route");
            setHgap(10);
            setVgap(6);
            editor.setOnAction( event -> {
                if (editor.getText().equals("Edit Route")) {
                    setChoosingState(true);
                    onlyCivilianShipsEnabled();
                    setChoosingTarget(this);
                    editor.setText("Accept");
                    joiner = new StringJoiner("-");
                    field.setText("");
                } else{
                    setChoosingState(false);
                    allEnabled();
                    btn.getModel().editRoute(parseRoute());
                    editor.setText("Edit Route");
                }
                });
                passengers = new ListView<>();
                vehicles = new ListView<>();
            show = new Button("Show Passenger");
            show.setOnAction(event -> {
                Passenger p = passengers.getSelectionModel().getSelectedItem();
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
            });
            dstr = new Button("Destroy Vehicle");
            dstr.setOnAction(event -> {
                if(vehicles.getSelectionModel().getSelectedItem() == null)
                    return;
                vehicles.getSelectionModel().getSelectedItem().destroy();
            });
                vBox = new VBox();
            vBox.setPrefHeight(100);
                pBox = new VBox();
            pBox.setPrefHeight(100);
                vBox.getChildren().add(new ScrollPane(vehicles));
                pBox.getChildren().add(new ScrollPane(passengers));

        }

        /**
         * Parses route of vehicle.
         * @return List of ports.
         */
        public List<? extends Port> parseRoute(){
            List<Port> list = new ArrayList<>();
            for (String s : field.getText().split("-")){
                list.add(portsSource.get(s));
            }
            return list;
        }

        /**
         * Sets details of vehicle in view
         * @param btn button with vehicle
         */
        public void setDetails(VehicleButton btn) {
            Vehicle v = btn.getModel();
            this.btn = btn;
            this.getChildren().clear();
            Map<String, String> map = v.getProperties();
            int i = 2;
            for (String key : map.keySet()) {
                add(new Text(key), 0, i);
                Label lab = new Label(map.get(key));
                lab.setWrapText(true);
                add(lab, 1, i);
                i++;
            }
            if (v instanceof Airliner) {
                Button landing = new Button("Emergency Landing");
                landing.setOnAction((event) -> {
                    System.out.println("Emergency Landing");
                    ((Airliner)btn.getModel()).emergencyLanding(civilianAirportMap.values());
                    landing.setDisable(true);
                });
                if(btn.getModel().isForcedRouteChange())
                    landing.setDisable(true);
                else
                    landing.setDisable(false);
                add(landing, 0, i++);
            }

            Button destroy = new Button("Destroy Vehicle");
            destroy.setOnAction((event) -> {
                mapPane.getChildren().remove(btn);
                btn.destroy();
                System.out.println("Destroying vehicle");

            });
            add(destroy, 0, i++);
            if(v instanceof CivilianVehicle)
                addPassengerView(i);
        }

        /**
         * Add list with passengers to view.
         * @param index location in panel
         */
        public void addPassengerView(int index){
            passengers.getItems().clear();
            passengers.getItems().addAll(((CivilianVehicle)btn.getModel()).getVehiclePassengers());
            add(new Text("Passengers"), 0, index++);
            add(pBox, 0, index++,2,1);
            add(show,0 , index);
        }

        /**
         * Make possibility to edit route.
         * @param portsSource
         */
        public void setEditableRoute( Map<String , ? extends Port> portsSource) {

            add(new Text("Route"), 0, 0);
            List<String> list = btn.getModel().getTravelRoute();
            joiner = new StringJoiner("\n");
            list.forEach(e -> joiner.add(e));
            field.setText(joiner.toString());
            add(field, 1, 0);
            add(editor, 1, 1);
            this.portsSource = portsSource;

        }

        @Override
        public void ChoiceHasBeenMade(String item) {
            String[] s = joiner.toString().split("-");
            if(!s[s.length-1].equals(item)){
                joiner.add(item);
                field.setText(joiner.toString());
            }
        }
    }
}
