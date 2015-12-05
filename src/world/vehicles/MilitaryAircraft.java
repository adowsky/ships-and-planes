package world.vehicles;

import javafx.geometry.Point2D;
import world.ports.MilitaryAirport;
import world.ports.Port;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents military aircraft.
 */
public class MilitaryAircraft extends Airplane {
    private ArmamentType armamentType;
    private List<MilitaryAirport> route;
    private int lastVisitedPortIndex;
    private MovingEngine<Vehicle> engine;

    /**
     * Creates MilitaryAircraft
     * @param location location on map
     * @param staffAmount number of staff on plane
     * @param maxFuel maximum value of fuel
     * @param armamentType armament type
     */
    public MilitaryAircraft(Point2D location,double speed, List<MilitaryAirport>route, int staffAmount, int maxFuel, ArmamentType armamentType){
        super(location,speed,staffAmount,maxFuel);
        this.armamentType=armamentType;
        this.route = route;
        lastVisitedPortIndex = 0;
    }

    /**
     * Returns armament type
     * @return armament type
     */
    public ArmamentType getArmamentType(){
        return  armamentType;
    }

    /**
     * Sets armament type
     * @param armamentType armament type
     */
    public void  setArmamentType(ArmamentType armamentType){
        this.armamentType=armamentType;
    }
    @Override
    public void arrivedToPort(){
        super.arrivedToPort();
        lastVisitedPortIndex++;
    }

    @Override
    public void Draw() {

    }

    @Override
    public MilitaryAirport getNextPort() {
        return ((lastVisitedPortIndex+1) < route.size()) ? route.get(lastVisitedPortIndex + 1) : route.get(lastVisitedPortIndex);
    }
    public void nextCrossing(){
        if(isOnRouteFinish()){
            getNextPort().aircraftArrive(this);
        }
        else{
            super.nextCrossing();
        }
    }
    @Override
    public Port getDestination() {
        return ((lastVisitedPortIndex+1) < route.size()) ? route.get(lastVisitedPortIndex + 1) : route.get(lastVisitedPortIndex);
    }
    @Override
    public Map<String, String > getProperties(){
        Map<String , String > map = new HashMap<>();
        return map;
    }
}
