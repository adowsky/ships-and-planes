package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import sun.rmi.server.InactiveGroupException;
import world.Cross;
import world.Crossing;
import world.ports.Harbour;
import world.ports.Port;
import world.vehicles.FerryBoat;
import world.vehicles.Vehicle;

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
    private VehicleDetails vehicleDetails;
    private EventHandler<ActionEvent> civilianShipClicked;
    private ChoosingController choosingTarget;
    private boolean choosingState = false;
    MapInitializer initializer;

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
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        Image map = new Image(getClass().getResourceAsStream("blank-world-map (1).jpg"));
        System.out.println("Initialization");
        //gc.setFill(Color.GREEN);
       // gc.fillOval(10,10,10,10);
        gc.drawImage(map,0,0);
        vehicleDetails = new VehicleDetails();
        civilianShipClicked = event -> {
            vehicleDetails.setDetails(((VehicleButton)event.getSource()).getModel());
            controlPanel.setCenter(vehicleDetails);
        };

        Map<String, Harbour> ports= null;
        try{
            initializer = new MapInitializer(getClass().getResource("map.xml").getPath());
            Set<PortButton<Harbour>> set = initializer.getHarbours();
            set.forEach((btn) ->{btn.getStyleClass().add("seaport-button");
            btn.setOnAction(event -> seaPortClick(null));});
            ports = initializer.getSeaPorts();
            mapPane.getChildren().addAll(set);
        }catch (ParserConfigurationException ex){
            ex.printStackTrace();
            //TODO wyłączenie apki
        }
        VehicleButton vhc = new VehicleButton();
        Harbour reykjavik = ports.get("Reykjavik");
        Harbour salvador = ports.get("Salvador");
        FerryBoat boat = new FerryBoat(reykjavik.getLocation(),0.01,Arrays.asList(reykjavik,ports.get("Salvador")),50,"KOMODO");
        vhc.setModel(boat);
        vhc.getStyleClass().add("civilian-ship");
        vhc.setOnAction(civilianShipClicked);
        mapPane.getChildren().add(vhc);
        boat.setRoute(reykjavik.getRouteToPort(salvador));
        boat.setReadyToTravel();

    }

    @FXML public synchronized void seaPortClick(ActionEvent event){
        Thread t;
        if(civilianPlane == null) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        civilianPlane = FXMLLoader.load(getClass().getResource("civilian_ship_form.fxml"));
                        Platform.runLater(()->controlPanel.setCenter(civilianPlane));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }
    @FXML public synchronized  void airPortClicked(ActionEvent event){
        if(civilianAirForm == null) {
            Thread t;
            t = new Thread(() -> {
                try {
                    civilianAirForm = FXMLLoader.load(getClass().getResource("military_ship_form.fxml"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(() -> controlPanel.setCenter(civilianAirForm));
            });
            t.setDaemon(true);
            t.start();
        }
    }

    class VehicleDetails extends GridPane{
     public void setDetails(Vehicle v){
         this.getChildren().clear();
         Map<String, String> map = v.getProperties();
         int i =0;
         for(String key : map.keySet()){
             add(new Text(key),0,i);
             add(new Label(map.get(key)),1,i);
             i++;
         }
     }
    }
    public void onlyCivilianShipsEnabled(){
        synchronized (enablingProtector) {

        }

    }
    public void allEnabled(){

    }
    public void setChoosingTarget(ChoosingController o){
        choosingTarget = o;
    }
    public void setChoosingState(boolean state){
        choosingState = state;
    }
    public void addVehicleButton(Map<String, String[]> details){

    }
    private void parseDetails(Map<String, String[]> details){
        int maxFuel = Integer.valueOf(details.get("Max fuel")[0]);
        int maxCapacity = Integer.valueOf(details.get("Max capacity")[0]);
        int speed = Integer.valueOf(details.get("Speed")[0]);
        int staffAmount = Integer.valueOf(details.get("Staff amount")[0]);
        String type = details.get("Type")[0];
        String[] route = details.get("Route");

            //FACTORY
        if(type.equals("FerryBoat")){
            List<Harbour> portList = new ArrayList<>();
            Map<String, Harbour> ports = initializer.getSeaPorts();
            for(String s: route){
                portList.add(ports.get(s));
            }
        }
    }
}
