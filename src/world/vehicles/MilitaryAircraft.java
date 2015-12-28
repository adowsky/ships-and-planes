package world.vehicles;

import javafx.geometry.Point2D;
import world.ports.MilitaryAirport;
import world.ports.Port;

import java.util.Collections;
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
    private boolean fromSea;

    /**
     * Creates MilitaryAircraft
     * @param location location on map
     * @param staffAmount number of staff on plane
     * @param maxFuel maximum value of fuel
     * @param armamentType armament type
     */
    public MilitaryAircraft(Point2D location,double speed, List<MilitaryAirport>route, int staffAmount, int maxFuel, ArmamentType armamentType, boolean formCarrier){
        super(location,speed,staffAmount,maxFuel);
        this.armamentType=armamentType;
        this.route = route;
        lastVisitedPortIndex = 0;
        if(!formCarrier)
            setRoute(route.get(0).getRouteToPort(route.get(1)));
        else {
            lastVisitedPortIndex = -1;
        }

        fromSea = formCarrier;
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
    public void setFromSea(){
        fromSea = true;
    }
    @Override
    public void Draw() {

    }
    @Override
    public MilitaryAirport getNextPort() {
        if(lastVisitedPortIndex == route.size()-1){
            if(fromSea) {
                route.remove(route.size() - 1);
                fromSea = false;
            }
            Collections.reverse(route);
            lastVisitedPortIndex = 0;
        }
        return ((lastVisitedPortIndex+1) < route.size()) ? route.get(lastVisitedPortIndex + 1) : route.get(lastVisitedPortIndex);
    }

    @Override
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

    @Override
    public Port getLastPort() {
        if(lastVisitedPortIndex<0)
            return null;
        return route.get(lastVisitedPortIndex);
    }
}
