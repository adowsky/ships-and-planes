package world.vehicles;

import world.Passenger;
import world.ports.Port;

import java.util.Collection;
import java.util.Set;

/**
 * Represents vehicle that can carry Passengers.
 */
public interface CivilianVehicle {
    /**
     * Returns Set of passengers that Vehicle contains.
     * @return set of passengers
     */
    Set<Passenger> getVehiclePassengers();

    /**
     * Wipes vehicle's list of passengers.
     */
    void clearPassengersList();

    /**
     * Returns Port that vehicle should visit next.
     * @return Port that vehicle should visit next.
     */
    Port getNextPort();

    /**
     * Adds collections of passengers to vehicle's passengers List.
     * @param collection set of passengers to add
     */
    void addPassengers(Collection<Passenger> collection);

    /**
     * Returns maximum value of Passengers that vehicle can contains.
     * @return maximum value of Passengers that vehicle can contains.
     */
    int getMaxPassengersAmount();
    void setReadyToTravel();

}
