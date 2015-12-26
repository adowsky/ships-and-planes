package world.ports;

import javafx.geometry.Point2D;
import world.vehicles.CivilianVehicle;
import world.Passenger;
import world.vehicles.Airplane;

import java.util.*;

/**
 * Class represents civilian airport that can interact with civilian planes.
 */
public class CivilianAirport extends AirPort implements CivilianPort{
    private List<CivilianVehicle> planesList;
    private Set<Passenger> passengersSet;

    private int timeToNextDeparture;
    /**
     * Creates civilian airport
     */
    public CivilianAirport(int timeToNextDeparture, int capacity, Point2D location){
        super(capacity,location);
        planesList = new ArrayList<>();
        passengersSet = new HashSet<>();
        this.timeToNextDeparture = timeToNextDeparture;
    }
    @Override
    public boolean isFull() {
        return planesList.size() >= getMaxCapacity();
    }

    @Override
    public void Draw() {

    }

    public synchronized <T extends Airplane & CivilianVehicle> void vehicleArrive(T vehicle) {
        canLand();
        vehicle.arrivedToPort();
        Set<Passenger> pList = vehicle.getVehiclePassengers();
        Set<Port> possiblePorts = getLandConnectionPorts();
        passengersService(pList,passengersSet,possiblePorts);
        vehicle.clearPassengersList();
        maintainVehicle(vehicle);
        vehicleDeparture(vehicle);
    }
    private void maintainVehicle(Airplane vehicle){
        vehicle.refuel();
        vehicle.setRoute(getRouteToPort(vehicle.getNextPort()));
        vehicle.maintenanceStart(timeToNextDeparture);
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

            List<CivilianPort> portListForPassenger = new ArrayList<>();
            for(Port p : getAllRoutes()){
                portListForPassenger.add((CivilianPort)p);
            }
            getLandConnectionPorts().forEach((p)-> portListForPassenger.add((CivilianPort)p));
        return portListForPassenger;
    }

    public boolean canLand() {
        while (planesList.size()>= getMaxCapacity()){
            try{
                Thread.sleep(1);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
        return true;
    }



    public void vehicleDeparture(CivilianVehicle vehicle) {
        Collection<Passenger> passengers = generatePassengersCollectionForVehicle(vehicle);
        vehicle.addPassengers(passengers);
        passengersSet.removeAll(passengers);
        vehicle.setReadyToTravel();
        planesList.remove(vehicle);
    }
    private Collection<Passenger> generatePassengersCollectionForVehicle(CivilianVehicle vehicle){
        Iterator<Passenger> iter = passengersSet.iterator();
        Collection<Passenger> newPassengersList = new HashSet<>();
        while(iter.hasNext()){
            Passenger passenger = iter.next();
            if(passenger.getLastVisitedPort() == vehicle.getNextPort()){
                newPassengersList.add(passenger);
            }
        }
        return  newPassengersList;
    }


}
