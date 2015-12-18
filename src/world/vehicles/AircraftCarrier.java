package world.vehicles;

import javafx.geometry.Point2D;
import world.ports.Port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class represents aircraft carrier.  It have ship's properties and it is a vehicle factory.
 */
public class AircraftCarrier extends Ship {
    private ArmamentType armament;
    private List<MilitaryAircraft> producedPlanes;

    /**
     * Creates airport.
     * @param location
     * @param maxVelocity
     * @param armament
     */
    public AircraftCarrier(Point2D location, double maxVelocity, ArmamentType armament){
        super(location,maxVelocity);
        this.armament=armament;
        producedPlanes = new ArrayList<>();
    }
    /**
     * Returns the armament type of the aircraft carrier's instance.
     * @return Armament type of the aircraft carrier's instance
     */
    public ArmamentType getArmament() {
        return armament;
    }

    /**
     * Sets armament type.
     * @param armament armament type to set.
     */
    public void setArmament(ArmamentType armament) {
        this.armament = armament;
    }


    @Override
    public void Draw() {

    }

    @Override
    public Port getNextPort() {
        return null;
    }

    @Override
    public Port getDestination() {
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String,String> map = new HashMap<>();
        return map;
    }

    @Override
    public Port getLastPort() {
        //TODO
        return null;
    }
}
