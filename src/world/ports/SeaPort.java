package world.ports;

import javafx.geometry.Point2D;

/**
 * Represent seaport.
 */
public abstract class SeaPort extends Port {
    /**
     * Creates Seaport.
     * @param capacity
     * @param location
     */
    public SeaPort(int capacity, Point2D location){
        super(capacity,location);
    }

}
