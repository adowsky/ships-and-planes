package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import world.Cross;
import world.Crossing;
import world.ports.Harbour;
import world.ports.Port;
import world.vehicles.FerryBoat;
import world.vehicles.SynchronizedUpdateNotifier;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * FXML Controller for application's window.
 */
public class FXMLWindowController implements Initializable {
    @FXML private Canvas canvas;
    @FXML private PortButton<Harbour> reykjavik;
    @FXML private PortButton<Harbour> miami;
    @FXML private List<PortButton<Harbour>> portList;
    @FXML private BorderPane controlPanel;
    @FXML private Pane mapPane;
    private Parent civilianPlane = null;
    private Parent civilianAirForm = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        Image map = new Image(getClass().getResourceAsStream("map.jpg"));
        System.out.println("Initialization");
        //gc.setFill(Color.GREEN);
       // gc.fillOval(10,10,10,10);
        gc.drawImage(map,0,0);
        initializePorts();
        VehicleButton vhc = new VehicleButton();
        FerryBoat boat = new FerryBoat(reykjavik.getLocation(),0.01,Arrays.asList(reykjavik.getModel(),miami.getModel()),50,"KOMODO");
        vhc.setModel(boat);
        vhc.getStyleClass().add("civilian-ship");
        mapPane.getChildren().add(vhc);
        boat.setRoute(reykjavik.getModel().getRouteToPort(miami.getModel()));
        boat.setReadyToTravel();
    }
    private void initializePorts(){
        Point2D location = new Point2D(reykjavik.getLayoutX(),reykjavik.getLayoutY());
        Map<Port,List<Cross>> portListMap = new HashMap<>();
        List<Cross> route = new ArrayList<>();
        Cross[] rout;
        Cross seaBot = new Crossing(344,233,5);

        Cross seaTop = new Crossing(289,116,5);
        Cross airAfrica = new Crossing(351,128,5);
        Cross airAustralia = new Crossing(606,247,5);
        Cross airChina = new Crossing(626,131,5);
        Cross airBengalBay = new Crossing(567,162,5);
        Cross airIndianOcean = new Crossing(499,197,5);
        Cross airDeepChina = new Crossing(568,116,5);
        Cross airIndia = new Crossing(533,118,5);
        Cross airIran = new Crossing(483,129,5);
        Cross airCaspian = new Crossing(454,91,5);
        Cross airRussia = new Crossing(436,69,5);
        Cross airGermany = new Crossing(393,74,6);
        reykjavik.setModel(new Harbour(500,10,location));
        location = new Point2D(miami.getLayoutX(),miami.getLayoutY());
        miami.setModel(new Harbour(500,10,location));
        rout = new Cross[]{seaTop,miami.getModel()};
        portListMap.put(miami.getModel(),Arrays.asList(rout));
        reykjavik.getModel().setWays(portListMap);
        rout = new Cross[]{seaTop,reykjavik.getModel()};
        portListMap = new HashMap<>();
        portListMap.put(reykjavik.getModel(),Arrays.asList(rout));
        miami.getModel().setWays(portListMap);
    }
    @FXML public synchronized void seaPortClick(ActionEvent event){
        Parent node = null;
        if(civilianPlane == null) {
            try {
                civilianPlane = FXMLLoader.load(getClass().getResource("civilian_ship_form.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        controlPanel.setCenter(civilianPlane);
    }
    @FXML public synchronized  void airPortClicked(ActionEvent event){
        if(civilianAirForm == null) {
            try {
                civilianAirForm = FXMLLoader.load(getClass().getResource("military_ship_form.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            }
        controlPanel.setCenter(civilianAirForm);
    }
}
