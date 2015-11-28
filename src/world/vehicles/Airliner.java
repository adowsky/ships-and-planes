package world.vehicles;

import javafx.geometry.Point2D;
import world.ports.CivilianAirport;
import world.Passenger;
import world.ports.Port;

import java.util.*;
import java.util.List;

/**
 * Represents airliner. Contains airplane's properties and can exchange passengers.
 */
public class Airliner extends Airplane implements CivilianVehicle {
    private int maxPassengersAmount;
    private Set<Passenger> passengersList;
    private List<CivilianAirport> route;
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

    public CivilianAirport getNextPort(){
        return ((lastVisitedPortIndex+1) < route.size()) ? route.get(lastVisitedPortIndex + 1) : route.get(lastVisitedPortIndex);
    }
    @Override
    public void arrivedToPort(){
        super.arrivedToPort();
        lastVisitedPortIndex++;
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
//TODO
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
    public Port getDestination() {
        return getNextPort();
    }
}
