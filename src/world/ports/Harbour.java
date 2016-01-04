package world.ports;

import javafx.geometry.Point2D;
import world.Passenger;
import world.vehicles.CivilianVehicle;
import world.vehicles.Ship;
import world.vehicles.Vehicle;

import java.util.*;

/**
 * Represents civilian sea port.
 */
public class Harbour extends SeaPort implements CivilianPort {
    private List<CivilianVehicle> shipsList;
    private Set<Passenger> passengersSet;
    private List<CivilianPort> portListForPassenger;
    private int timeToNextDeparture;

    /**
     * Creates Harbour
     * @param timeToNextDeparture time to next departure
     * @param capacity Maximum capacity of port
     * @param location Location of the Port.
     */
    public Harbour(int timeToNextDeparture, int capacity, Point2D location){
        super(capacity, location);
        shipsList = new ArrayList<>();
        passengersSet= new HashSet<>();
        this.timeToNextDeparture=timeToNextDeparture;
    }

    @Override
    public void Draw() {

    }

    /**
     * Services arrival of vehicle.
     * @param vehicle
     * @param <T> type of vehicle.
     */
    public <T extends Ship & CivilianVehicle> void vehicleArrive(T vehicle) {
        canLand();
        shipsList.add(vehicle);
        vehicle.arrivedToPort();
        vehicle.addDestroyListener(this);
        Set<Passenger> pList = vehicle.getVehiclePassengers();
        synchronized (this) {
            passengersService(pList, passengersSet, getLandConnectionPorts());
        }
        vehicle.clearPassengersList();
        maintainVehicle(vehicle);
        vehicle.setRoute(getRouteToPort(vehicle.getNextPort()));
        vehicleDeparture(vehicle);
    }

    /**
     * Adds newly produced vehicle to the port.
     * @param vehicle new Vehicle.
     * @param <T> type of Vehicle.
     */
    public <T extends Ship & CivilianVehicle> void addNewlyProducedVehicle(T vehicle){
        canLand();
        shipsList.add(vehicle);
        vehicle.clearPassengersList();
        vehicle.setRoute(getRouteToPort(vehicle.getNextPort()));
        vehicleDeparture(vehicle);
    }
    /**
     * Start maintenance of vehicle
     * @param vehicle vehicle to maintain.
     */
    private void maintainVehicle(Ship vehicle){
        vehicle.maintenanceStart(timeToNextDeparture);
    }

    /**
     * Services departure of vehicle.
     * @param vehicle vehicle that is going to departure.
     */
    private <T extends Ship & CivilianVehicle> void vehicleDeparture(T vehicle){
        Port nextPort = vehicle.getNextPort();
        Collection<Passenger> newPassengersList = new HashSet<>();
        for (Passenger p : passengersSet){
            if(!p.isWaiting() && p.getNextPortToVisit() == nextPort){
                newPassengersList.add(p);
            }
        }
        synchronized (this) {
            passengersSet.removeAll(newPassengersList);
        }
        vehicle.addPassengers(newPassengersList);
        vehicle.setReadyToTravel();
        shipsList.remove(vehicle);
        vehicle.removeDestroyListener(this);
    }

    @Override
    public void passengerHasCome(Passenger passenger) {
        passengersSet.add(passenger);
    }

    @Override
    public void passengerWentAway(Passenger passenger) {
        passengersSet.remove(passenger);
    }

    @Override
    public List<CivilianPort> getAllConnections() {
        if(portListForPassenger == null) {
            portListForPassenger = new ArrayList<>();
            for(Port p : getAllRoutes()){
                portListForPassenger.add((CivilianPort)p);
            }
            getLandConnectionPorts().forEach((p)-> portListForPassenger.add((CivilianPort)p));
        }
        return  portListForPassenger;

    }

    @Override
    public Collection<Passenger> getPassengers() {
        return passengersSet;
    }

    @Override
    public Collection<? extends Vehicle> getVehicles() {
        List<Vehicle> l = new LinkedList<>();
        shipsList.forEach(e -> l.add((Vehicle)e));
        return l;
    }

    /**
     * Checks when vehicle can land.
     * @return if vehicle can land.
     */
    public synchronized boolean canLand() {
        while (shipsList.size()>= getMaxCapacity()){
            try{
                Thread.sleep(1);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
        return true;
    }
    @Override
    public boolean isFull(){
        return shipsList.size()>= getMaxCapacity();
    }

    @Override
    public void objectDestroyed(Vehicle o) {
        shipsList.remove(o);
    }
}
