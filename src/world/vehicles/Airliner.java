package world.vehicles;

import javafx.geometry.Point2D;
import world.Passenger;
import world.ports.CivilianAirport;
import world.ports.Port;

import java.util.*;

/**
 * Represents airliner. Contains airplane's properties and can exchange passengers.
 */
public class Airliner extends Airplane implements CivilianVehicle {
    private int maxPassengersAmount;
    private Set<Passenger> passengersList;
    private List<CivilianAirport> route;
    private List<CivilianAirport> newRoute;
    private boolean routeChanged;
    private boolean modifyRoute;
    private int lastVisitedPortIndex;


    /**
     * Creates airliner
     * @param location point on map
     * @param route list of ports to visit
     * @param staffAmount number of staff on plane
     * @param maxFuel maximum value of fuel
     * @param maxPassengersAmount maximum value of passengers
     */
    public Airliner(Point2D location,double speed,List<CivilianAirport> route,int staffAmount,int maxFuel,int maxPassengersAmount){
        super(location,speed,staffAmount,maxFuel);
        this.maxPassengersAmount=maxPassengersAmount;
        passengersList= new HashSet<>();
        this.route = route;
        setRoute(route.get(0).getRouteToPort(route.get(1)));
        lastVisitedPortIndex = 0;
    }

    @Override
    public Set<Passenger> getVehiclePassengers() {
        return passengersList;
    }

    @Override
    public void clearPassengersList() {
        passengersList.clear();
    }

    /**
     * Returns next Airport on list.
     * @return next Airport on list.
     */
    public CivilianAirport getNextPort(){
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
    public void arrivedToPort(){
        super.arrivedToPort();
        if(!isForcedRouteChange()) {
            lastVisitedPortIndex++;
        }else{
            clearForcedRouteChange();
        }
    }
    @Override
    public void addPassengers(Collection<Passenger> collection) {
        passengersList.addAll(collection);
    }

    @Override
    public int getMaxPassengersAmount() {
        return maxPassengersAmount;
    }


    /**
     * Sets maximum number of passengers.
     * @param maxPassengersAmount  maximum number of passengers.
     */
    public void setMaxPassengersAmount(int maxPassengersAmount) {
        this.maxPassengersAmount = maxPassengersAmount;
    }

    @Override
    public void Draw() {
    }

    @Override
    public void nextCrossing(){
        if(isOnRouteFinish()){
            if(!isForcedRouteChange())
                getNextPort().vehicleArrive(this);
            else{
                ((CivilianAirport)getLastRotueStop()).vehicleArrive(this);
            }
        }
        else{
            super.nextCrossing();
        }
    }

    @Override
    public Port getDestination() {
        return getNextPort();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String , String > map = new HashMap<>();
        map.put("Max Capacity: ",Integer.toString(maxPassengersAmount));
        map.put("Passengers Amount: ", Integer.toString(passengersList.size()));
        map.put("Location: ",
                Double.toString(getLocation().getX()).split("\\.")[0]+"x"+Double.toString(getLocation().getY()).split("\\.")[0]);
        map.put("ID: ",Integer.toString(getId()));
        map.put("Staff Amount: ",Integer.toString(getStaffAmount()));
        String[] cFuel = Double.toString(getCurrentFuel()).split("\\.");
        String fuel = cFuel[0]+"."+cFuel[1].substring(0,1);
        String maxFuel = Double.toString(getMaxFuel()).split("\\.")[0];
        map.put("Fuel: ",fuel+"/"+maxFuel);
        return map;
    }

    @Override
    public Port getLastPort() {
        return route.get(lastVisitedPortIndex);
    }

    @Override
    public void decreasePortIndex() {
        if(lastVisitedPortIndex == 0){
            Collections.reverse(route);
            lastVisitedPortIndex = route.size()-2;
        }else{
            lastVisitedPortIndex--;
        }
    }

    @Override
    public List<String> getTravelRoute() {
        List<String> list = new LinkedList<>();
        route.forEach(e -> list.add(e.getName()));
        return list;
    }

    @Override
    public void editRoute(List<? extends Port> route) {
        newRoute = (List<CivilianAirport>)route;
        routeChanged = true;
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

    /**
     * Forces landing in nearest Port.
     * @param db Database of ports to find the nearest.
     */
    public void emergencyLanding(Collection<CivilianAirport> db){
        boolean found = false;
        CivilianAirport target = null;
        int r = 0;
        while(!found){
            r += 5;
            Circle area = new Circle(getLocation().getX(),getLocation().getY(),r);
            for(CivilianAirport o : db){
                if(area.getBounds().contains(o.getX(),o.getY())){
                    found = true;
                    target = o;
                    break;
                }
            }
        }
        List<CivilianAirport> list = new ArrayList<>();
        list.add(target);
        changeRoute(list);
    }
}
