package world.vehicles;

import gui.PortButton;
import gui.VehicleButton;
import world.ports.CivilianAirport;
import world.ports.Harbour;
import world.ports.MilitaryAirport;
import world.vehicles.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *  Parser which process data from serialized collection and returns object used in application.
 */
public class SerializationParser {
    private Set<Serializable> raw;
    private Set<PortButton> parsedPortButtons;
    private Set<VehicleButton> parsedVehicleButtons;
    public SerializationParser(Set<Serializable> set){
        raw = set;
    }

    /**
     * Parses the collection.
     */
    public void parse(){
        parsedPortButtons = new HashSet<>();
        parsedVehicleButtons = new HashSet<>();
        for(Serializable o : raw){
            if(o instanceof CivilianAirport){
                parsedPortButtons.add(makeCivilianAirport((CivilianAirport)o));
            }else if(o instanceof MilitaryAirport){
                parsedPortButtons.add(makeMilitaryAirport((MilitaryAirport)o));
            }else if(o instanceof Harbour){
                parsedPortButtons.add(makeHarbour((Harbour)o));
            }else if (o instanceof AircraftCarrier) {
                parsedVehicleButtons.add(makeAircraftCarrier((AircraftCarrier)o));
            } else if (o instanceof MilitaryAircraft) {
                parsedVehicleButtons.add(makeMilitaryAircraft((MilitaryAircraft)o));
            } else if (o instanceof FerryBoat) {
                parsedVehicleButtons.add(makeFerry((FerryBoat)o));
            } else if (o instanceof Airliner) {
                parsedVehicleButtons.add(makeAirliner((Airliner)o));
            }
        }

    }
    private PortButton<MilitaryAirport> makeMilitaryAirport(MilitaryAirport o){
        PortButton<MilitaryAirport> btn = new PortButton<>();
        btn.setModel(o);
        btn.setLayoutX(o.getLocation().getX());
        btn.setLayoutY(o.getLocation().getY());
        btn.setName("military airport");
        btn.getStyleClass().add("military-airport-button");
        return btn;
    }
    private PortButton<Harbour> makeHarbour(Harbour o){
        PortButton<Harbour> btn = new PortButton<>();
        btn.setModel(o);
        btn.setLayoutX(o.getLocation().getX());
        btn.setLayoutY(o.getLocation().getY());
        btn.setName("harbour");
        btn.getStyleClass().add("seaport-button");
        return btn;
    }
    private VehicleButton makeAircraftCarrier(AircraftCarrier o){
        VehicleButton btn = new VehicleButton();
        btn.setModel(o);
        btn.getStyleClass().add("military-ship");
        btn.setLayoutX(o.getLocation().getX());
        btn.setLayoutY(o.getLocation().getY());
        return btn;
    }
    private VehicleButton makeMilitaryAircraft(MilitaryAircraft o){
        VehicleButton btn = new VehicleButton();
        btn.getStyleClass().add("military-plane");
        btn.setModel(o);
        btn.setLayoutX(o.getLocation().getX());
        btn.setLayoutY(o.getLocation().getY());
        return btn;
    }
    private VehicleButton makeFerry(FerryBoat o){
        VehicleButton btn = new VehicleButton();
        btn.getStyleClass().add("civilian-ship");
        btn.setModel(o);
        btn.setLayoutX(o.getLocation().getX());
        btn.setLayoutY(o.getLocation().getY());
        return btn;
    }
    private VehicleButton makeAirliner(Airliner o){
        VehicleButton btn = new VehicleButton();
        btn.setModel(o);
        btn.setLayoutX(o.getLocation().getX());
        btn.setLayoutY(o.getLocation().getY());
        btn.getStyleClass().add("civilian-plane");
        return btn;
    }
    /**
     * Returns Set of Port buttons with model type in name field.
     */
    public Set<PortButton> getPortButtonsWithName(){
        if(parsedPortButtons == null)
            parsedPortButtons = new HashSet<>();
        return parsedPortButtons;
    }
    private PortButton<CivilianAirport> makeCivilianAirport(CivilianAirport o){
        PortButton<CivilianAirport> btn = new PortButton<>();
        btn.setName("civilian airport");
        btn.getStyleClass().add("airport-button");
        btn.setLayoutX(o.getLocation().getX());
        btn.setLayoutY(o.getLocation().getY());
        btn.setModel(o);
        return btn;
    }

    /**
     * Returns Set of Vehicle buttons with model type in name field.
     * @return Set of Vehicle buttons with model type in name field.
     */
    public Set<VehicleButton> getVehicleButtonsWithName(){
        if(parsedVehicleButtons == null)
            parsedVehicleButtons = new HashSet<>();
        return parsedVehicleButtons;
    }
}
