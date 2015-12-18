package world.ports;

import javafx.geometry.Point2D;
import world.Cross;

import java.util.List;
import java.util.Map;

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
