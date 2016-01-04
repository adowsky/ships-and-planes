package world.vehicles;

import javafx.geometry.Point2D;
import world.ports.MilitaryAirport;
import world.ports.Port;
import world.vehicles.movement.RegisteringPortAdapter;

import java.util.*;

/**
 * Represents military aircraft.
 */
public class
MilitaryAircraft extends Airplane {
    private ArmamentType armamentType;
    private List<MilitaryAirport> route;
    private List<MilitaryAirport> newRoute;
    private int lastVisitedPortIndex;
    private Port portAdapter;
    private boolean fromSea;
    private boolean routeChanged;
    private boolean modifyRoute;

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

    @Override
    public void arrivedToPort(){
        super.arrivedToPort();
        lastVisitedPortIndex++;
        fromSea = false;
    }
    @Override
    public void Draw() {

    }
    @Override
    public MilitaryAirport getNextPort() {
        if(lastVisitedPortIndex == route.size()-1){
            if(modifyRoute){
                route.remove(0);
                modifyRoute = false;
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
        map.put("Armament Type: ", getArmamentType().toString());
        map.put("Location: ",
                Double.toString(getLocation().getX()).split("\\.")[0]+"x"+Double.toString(getLocation().getY()).split("\\.")[0]);
        map.put("ID: ",Integer.toString(getId()));
        map.put("Staff Amount: ",Integer.toString(getStaffAmount()));
        return map;
    }

    @Override
    public Port getLastPort() {
        if(lastVisitedPortIndex<0)
            return portAdapter;
        return route.get(lastVisitedPortIndex);
    }

    @Override
    public List<String> getTravelRoute() {
        List<String> list = new LinkedList<>();
        route.forEach(e -> list.add(e.getName()));
        return list;
    }

    @Override
    public void editRoute(List<? extends Port> route) {
        routeChanged = true;
        newRoute = (List<MilitaryAirport>)route;
        if(newRoute.get(0) != getNextPort()) {
            newRoute.add(0, getNextPort());
            modifyRoute = true;
        }
    }
    @Override
    public void maintenanceStart(int sleepTime){
        if(routeChanged){
            route = newRoute;
            newRoute = null;
            routeChanged = false;
            lastVisitedPortIndex = 0;

        }
        super.maintenanceStart(sleepTime);
    }
    public void setAbstractStartRoute(RegisteringPortAdapter adapter){
        portAdapter = adapter;
        setLocation(adapter.getX(),adapter.getY());
    }

}
