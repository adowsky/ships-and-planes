package world.ports;

import javafx.geometry.Point2D;
import world.Cross;
import world.vehicles.MilitaryAircraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents military airport. It is a port.
 */
public class MilitaryAirport extends AirPort {
    private List<MilitaryAircraft> planesList;
    private int timeToNextDeparture;
    public MilitaryAirport(int timeToNextDeparture, int capacity, Point2D location){
        super(capacity,location);
        this.timeToNextDeparture=timeToNextDeparture;
        planesList=new ArrayList<>();
    }
    public void aircraftArrive(MilitaryAircraft plane){
        if(isFull())
            return;
        plane.arrivedToPort();
        planesList.add(plane);
        maintainAircraft(plane);
        plane.setRoute(getRouteToPort(plane.getNextPort()));
        vehicleDeparture(plane);
    }
     private void maintainAircraft(MilitaryAircraft plane){
        plane.refuel();
        plane.maintenanceStart(timeToNextDeparture);
    }
    private void vehicleDeparture(MilitaryAircraft vehicle){
        vehicle.setReadyToTravel();
        planesList.remove(vehicle);
    }

    @Override
    public boolean isFull() {
       return (planesList.size()>=getMaxCapacity());
    }



    @Override
    public void Draw() {

    }
}
