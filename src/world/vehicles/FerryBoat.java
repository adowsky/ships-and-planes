package world.vehicles;

import javafx.geometry.Point2D;
import world.ports.Harbour;
import world.Passenger;
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
    private long arrivalTime;
    private boolean onMove;
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
        onMove=false;
        arrivalTime=0;
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
     * Sets maximum amount of passengers.
     * @param maxPassengersAmount maximum amount of passengers
     */
    public void setMaxPassengersAmount(int maxPassengersAmount) {
        this.maxPassengersAmount = maxPassengersAmount;
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
                Double.toString(getLocation().getX()).substring(0,5)+"x"+Double.toString(getLocation().getY()).substring(0,5));
        props.put("ID: ",Integer.toString(getId()));
        return props;
    }


}
