package world.ports;

import javafx.geometry.Point2D;
import world.Cross;

import java.util.List;
import java.util.Map;

/**
 * Represent seaport.
 */
public abstract class SeaPort extends Port {
    public SeaPort(int capacity, Point2D location, Map<Port,List<Cross>> ways){
        super(capacity,location, ways);
    }
    @Override
    public boolean isAir(){
        return false;
    }
}
