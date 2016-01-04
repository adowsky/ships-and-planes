package world.ports;

import javafx.geometry.Point2D;

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
