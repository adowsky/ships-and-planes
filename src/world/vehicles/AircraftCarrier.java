package world.vehicles;

import javafx.geometry.Point2D;
import world.Cross;
import world.ports.Harbour;
import world.ports.Port;
import world.vehicles.movement.MovingEngineTypes;

import java.util.*;

/**
 * Class represents aircraft carrier.  It have ship's properties and it is a vehicle factory.
 */
public class AircraftCarrier extends Ship {
    private ArmamentType armament;
    private List<MilitaryAircraft> producedPlanes;
    private Port lastVisitedPort;
    private Port nextPort;

    /**
     * Creates airport.
     * @param location
     * @param maxVelocity
     * @param armament
     */
    public AircraftCarrier(Point2D location, double maxVelocity, ArmamentType armament){
        super(location,maxVelocity, MovingEngineTypes.DODGING_PORTS);
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
    public void nextCrossing(){
        if(isOnRouteFinish()){
            removeFromPreviousCrossingRegister();
            Port newPort = randNewPort();
            setRoute(nextPort.getRouteToPort(newPort));
            lastVisitedPort = nextPort;
            nextPort = newPort;
        }
        else{
            super.nextCrossing();
        }
    }
    private Port randNewPort(){
        Set<Port> rout = getNextPort().getAllRoutes();
        Random random = new Random();
        int target = random.nextInt(rout.size());
        int i = 0;
        for(Port p : rout){
            if(i==target){
                return p;
            }
            i++;
        }
        return null;
    }

    @Override
    public Port getNextPort() {
        return nextPort;
    }

    @Override
    public Port getDestination() {
        return getNextPort();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String,String> map = new HashMap<>();
        return map;
    }

    @Override
    public Port getLastPort() {
        return lastVisitedPort;
    }
}
