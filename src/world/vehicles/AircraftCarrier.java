package world.vehicles;

import javafx.geometry.Point2D;
import world.ports.Port;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents aircraft carrier.  It have ship's properties and it is a vehicle factory.
 */
public class AircraftCarrier extends Ship {
    private ArmamentType armament;
    private List<MilitaryAircraft> producedPlanes;
    public AircraftCarrier(Point2D location, int maxVelocity, ArmamentType armament){
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
}
