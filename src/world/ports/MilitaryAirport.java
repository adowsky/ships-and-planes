package world.ports;

import javafx.geometry.Point2D;
import world.Passenger;
import world.vehicles.MilitaryAircraft;
import world.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents military airport. It is a port.
 */
public class MilitaryAirport extends AirPort {
    private List<MilitaryAircraft> planesList;
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
        planesList=new ArrayList<>();
        MilitaryAirportContainer.getInstance().addToSet(this);
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
        maintainAircraft(plane);
        plane.setRoute(getRouteToPort(plane.getNextPort()));
        vehicleDeparture(plane);
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
