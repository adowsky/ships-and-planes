package world.ports;


import world.passenger.Passenger;

import java.util.Collection;
import java.util.List;

/**
 * Port which contains Passengers and can service them.
 */
public interface CivilianPort {

    /**
     * Adds Passenger to the Port.
     * @param passenger Passenger to add.
     */
    void passengerHasCome(Passenger passenger);

    /**
     * Removes passenger form the Port.
     * @param passenger Passenger to remove.
     */
    void passengerWentAway(Passenger passenger);

    /**
     * Manages Passengers after vehicle's arrival to Port.
     * @param pList Passengers list from vehicle.
     * @param passengersSet passengers set of Port.
     * @param possiblePorts set of ports that current Port is connected with.
     */
    default void passengersService(Collection<Passenger> pList,Collection<Passenger> passengersSet,Collection<Port> possiblePorts){
        for(Passenger p : pList){
            p.nextPortIsVisited();
            if(possiblePorts.contains(p.getNextPortToVisit())){
                p.moveToTheNextPortNow();
            }
            else {
                passengersSet.add(p);
            }
        }
    }

    /**
     * Returns map of routes for all ports connected with this port.
     * @return map of routes for all ports connected with this port.
     */
    List<CivilianPort> getAllConnections();

    /**
     * Returns passengers in port.
     * @return   passengers in port.
     */
    Collection<Passenger> getPassengers();
}
