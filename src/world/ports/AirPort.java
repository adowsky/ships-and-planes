package world.ports;

import javafx.geometry.Point2D;
import world.Cross;

import java.util.List;
import java.util.Map;

/**
 * Represent airport.
 */
public abstract class AirPort extends Port {
    public AirPort(int capacity, Point2D location){
        super(capacity,location);
    }

}
