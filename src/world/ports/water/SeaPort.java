package world.ports.water;

import javafx.geometry.Point2D;
import world.ports.Port;

/**
 * Represent seaport.
 */
public abstract class SeaPort extends Port {
    /**
     * Creates Seaport.
     * @param capacity capacity
     * @param location location
     */
    public SeaPort(int capacity, Point2D location){
        super(capacity,location);
    }

}
