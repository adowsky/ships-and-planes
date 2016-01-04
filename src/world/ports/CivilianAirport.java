package world.ports;

import javafx.geometry.Point2D;
import world.Passenger;
import world.vehicles.Airliner;
import world.vehicles.Airplane;
import world.vehicles.CivilianVehicle;
import world.vehicles.Vehicle;

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

    /**
     * Process vehicle arrival.
     * @param vehicle vehicle which is arriving
     * @param <T> type of vehicle
     */
    public synchronized <T extends Airplane & CivilianVehicle> void vehicleArrive(T vehicle) {
        canLand();
        vehicle.arrivedToPort();
        planesList.add(vehicle);
        vehicle.addDestroyListener(this);
        Set<Passenger> pList = vehicle.getVehiclePassengers();
        Set<Port> possiblePorts = getLandConnectionPorts();
        passengersService(pList, passengersSet, possiblePorts);
        vehicle.clearPassengersList();
        maintainVehicle(vehicle);
        vehicleDeparture(vehicle);
    }
    /**
     * Adds newly produced vehicle to the port.
     * @param vehicle new Vehicle.
     * @param <T> type of Vehicle.
     */
    public <T extends Airplane & CivilianVehicle> void addNewlyProducedVehicle(T vehicle){
        canLand();
        planesList.add(vehicle);
        vehicle.addDestroyListener(this);
        vehicle.clearPassengersList();
        vehicle.setRoute(getRouteToPort(vehicle.getNextPort()));
        vehicle.refuel();
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

    @Override
    public Collection<Passenger> getPassengers() {
        return passengersSet;
    }

    @Override
    public Collection<? extends Vehicle> getVehicles() {
        List<Airliner> l = new LinkedList<>();
        planesList.forEach(e->l.add((Airliner)e));
        return l;
    }

    /**
     * Waits for opportunity to land and returns state of it.
     * @return  if vehicle can land in PORT.
     */
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

    /**
     * Process vehicle departure.
     * @param vehicle
     */
    public  <T extends Airplane & CivilianVehicle> void vehicleDeparture(T vehicle) {
        Collection<Passenger> passengers = generatePassengersCollectionForVehicle(vehicle);
        vehicle.addPassengers(passengers);
        passengersSet.removeAll(passengers);
        vehicle.setReadyToTravel();
        planesList.remove(vehicle);
        vehicle.removeDestroyListener(this);
    }

    private Collection<Passenger> generatePassengersCollectionForVehicle(CivilianVehicle vehicle){
        Iterator<Passenger> iter = passengersSet.iterator();
        Collection<Passenger> newPassengersList = new HashSet<>();
        while(iter.hasNext()){
            Passenger passenger = iter.next();
            if(!passenger.isWaiting() && passenger.getNextPortToVisit() == vehicle.getNextPort()){
                newPassengersList.add(passenger);
            }
        }
        return  newPassengersList;
    }


    @Override
    public void objectDestroyed(Vehicle o) {
        planesList.remove(o);
    }
}
