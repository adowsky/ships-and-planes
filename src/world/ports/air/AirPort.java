package world.ports.air;

import javafx.geometry.Point2D;
import world.ports.Port;

/**
 * Represent airport.
 */
public abstract class AirPort extends Port {
    /**
     * Creates airport
     * @param capacity
     * @param location
     */
    public AirPort(int capacity, Point2D location){
        super(capacity,location);
    }

}
