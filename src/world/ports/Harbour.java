package world.ports;

import javafx.geometry.Point2D;
import world.Cross;
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
    private int timeToNextDeparture;
    public Harbour(int timeToNextDeparture, int capacity, Point2D location, Map<Port,List<Cross>> ways){
        super(capacity, location, ways);
        shipsList = new ArrayList<>();
        passengersSet= new HashSet<>();
        this.timeToNextDeparture=timeToNextDeparture;
    }

    @Override
    public boolean isMilitary() {
        return false;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isAir() {
        return false;
    }

    @Override
    public void Draw() {

    }

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
    private void maintainVehicle(Ship vehicle){
        vehicle.maintenanceStart(timeToNextDeparture);
    }
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
    }

    @Override
    public void passengerHasCome(Passenger passenger) {

    }

    @Override
    public void passengerWentAway(Passenger passenger) {

    }

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
