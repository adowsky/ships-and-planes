package world.vehicles;

import javafx.geometry.Point2D;
import world.Passenger;
import world.ports.Harbour;
import world.ports.Port;

import java.util.*;

/**
 * Represents civilian ship.
 */
public class FerryBoat extends Ship implements CivilianVehicle {
    private int maxPassengersAmount;
    private Set<Passenger> passengersList;
    private List<Harbour> route;
    private String company;
    private List<Harbour> newRoute;
    private boolean routeChanged;
    private boolean modifyRoute;
    private int lastVisitedPortIndex;

    /**
     * Creates FerryBoat
     * @param location location on map
     * @param route list of ports
     * @param maxVelocity maximum value of fuel
     * @param maxPassengersAmount maximum value of passengers
     * @param company company's name
     */
    public FerryBoat(Point2D location, double maxVelocity, List<Harbour> route,int maxPassengersAmount, String company){
        super(location,maxVelocity);
        this.maxPassengersAmount=maxPassengersAmount;
        this.company=company;
        this.route = route;
        setRoute(route.get(0).getRouteToPort(route.get(1)));
        lastVisitedPortIndex = 0;
        passengersList = new HashSet<>();
    }

    @Override
    public int getMaxPassengersAmount() {
        return maxPassengersAmount;
    }


    /**
     * Returns Company that owns the ship.
     * @return Company that owns the ship
     */
    public String getCompany() {
        return company;
    }

    /**
     *  sets Company that owns the ship.
     * @param company Company that owns the ship.
     */
    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public void Draw() {

    }

    @Override
    public Set<Passenger> getVehiclePassengers() {
        return passengersList;
    }

    @Override
    public void clearPassengersList() {
        passengersList.clear();
    }

    @Override
    public Harbour getNextPort(){
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
    public Port getDestination() {
        return getNextPort();
    }

    @Override
    public void addPassengers(Collection<Passenger> collection) {
        passengersList.addAll(collection);
    }
    @Override
    public void nextCrossing(){
        if(isOnRouteFinish()){
            removeFromPreviousCrossingRegister();
            getNextPort().vehicleArrive(this);
        }
        else{
            super.nextCrossing();
        }
    }
    @Override
    public void arrivedToPort(){
        super.arrivedToPort();
        lastVisitedPortIndex++;
    }

    @Override
    public Map<String,String> getProperties(){
        Map<String,String> props = new HashMap<>();
        props.put("Max Capacity: ",Integer.toString(maxPassengersAmount));
        props.put("Passengers Amount: ", Integer.toString(passengersList.size()));
        props.put("Company: ",company);
        props.put("Location: ",
                Double.toString(getLocation().getX()).split("\\.")[0]+"x"+Double.toString(getLocation().getY()).split("\\.")[0]);
        props.put("ID: ",Integer.toString(getId()));
        return props;
    }

    @Override
    public Port getLastPort() {
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
        newRoute = (List<Harbour>)route;
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
}
