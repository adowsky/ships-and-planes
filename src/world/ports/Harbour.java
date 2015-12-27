package world.ports;

import javafx.geometry.Point2D;
import world.vehicles.CivilianVehicle;
import world.Passenger;
import world.vehicles.Ship;

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
     * @param timeToNextDeparture
     * @param capacity
     * @param location
     */
    public Harbour(int timeToNextDeparture, int capacity, Point2D location){
        super(capacity, location);
        shipsList = new ArrayList<>();
        passengersSet= new HashSet<>();
        this.timeToNextDeparture=timeToNextDeparture;
    }

    @Override
    public boolean isFull() {
        return false;
    }


    @Override
    public void Draw() {

    }

    /**
     * Services arrival of vehicle.
     * @param vehicle
     * @param <T> type of vehicle.
     */
    public synchronized  <T extends Ship & CivilianVehicle> void vehicleArrive(T vehicle) {
        canLand();
        vehicle.arrivedToPort();
        Set<Passenger> pList = vehicle.getVehiclePassengers();
        passengersService(pList,passengersSet,getLandConnectionPorts());
        vehicle.clearPassengersList();
        maintainVehicle(vehicle);
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
    private void vehicleDeparture(CivilianVehicle vehicle){
        Port nextPort = vehicle.getNextPort();
        Collection<Passenger> newPassengersList = new HashSet<>();
        for (Passenger p : passengersSet){
            if(p.getNextPortToVisit() == nextPort){
                newPassengersList.add(p);
            }
        }
        passengersSet.removeAll(newPassengersList);
        vehicle.addPassengers(newPassengersList);
        vehicle.setReadyToTravel();
        shipsList.remove(vehicle);
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
}
