package world.ports;

import javafx.geometry.Point2D;
import world.Passenger;
import world.vehicles.MilitaryAircraft;
import world.vehicles.Vehicle;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents military airport. It is a port.
 */
public class MilitaryAirport extends AirPort {
    private Set<MilitaryAircraft> planesList;
    private int timeToNextDeparture;

    /**
     * Creates Miliatry airport
     * @param timeToNextDeparture
     * @param capacity
     * @param location
     */
    public MilitaryAirport(int timeToNextDeparture, int capacity, Point2D location){
        super(capacity,location);
        this.timeToNextDeparture=timeToNextDeparture;
        planesList=new HashSet<>();
    }

    /**
     * Services aircraft arrival.
     * @param plane
     */
    public void aircraftArrive(MilitaryAircraft plane){
        if(isFull())
            return;
        plane.arrivedToPort();
        planesList.add(plane);
        plane.addDestroyListener(this);
        maintainAircraft(plane);
        plane.setRoute(getRouteToPort(plane.getNextPort()));
        vehicleDeparture(plane);
    }

    /**
     * Adds newly produced plane to the Port.
     * @param vehicle  newly produced plane
     */
    public void addNewlyProducedVehicle(MilitaryAircraft vehicle){
        canLand();
        vehicle.addDestroyListener(this);
        planesList.add(vehicle);
        vehicle.setRoute(getRouteToPort(vehicle.getNextPort()));
        vehicleDeparture(vehicle);
    }

    /**
     * Wait for possibility of landing and returns state of it.
     * @return if vehicle can land.
     */
    public synchronized boolean canLand() {
        while (planesList.size()>= getMaxCapacity()){
            try{
                Thread.sleep(1);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
        return true;
    }
    /**
     * Maintains aircraft
     * @param plane
     */
     private void maintainAircraft(MilitaryAircraft plane){
        plane.refuel();
        plane.maintenanceStart(timeToNextDeparture);
    }

    /**
     * Services departure of aircraft.
     * @param vehicle
     */
    private void vehicleDeparture(MilitaryAircraft vehicle){
        vehicle.setReadyToTravel();
        planesList.remove(vehicle);
        vehicle.removeDestroyListener(this);
    }

    @Override
    public boolean isFull() {
       return (planesList.size()>=getMaxCapacity());
    }

    @Override
    public Collection<Passenger> getPassengers() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Vehicle> getVehicles() {
        return planesList;
    }


    @Override
    public void Draw() {

    }

    @Override
    public void objectDestroyed(Vehicle o) {
        planesList.remove(o);
    }

}
