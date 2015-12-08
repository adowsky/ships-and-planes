package world.vehicles;

import javafx.geometry.Point2D;
import world.World;

import java.util.Set;

/**
 * Represents Ship.
 */
public abstract class Ship extends Vehicle {
    /**
     * Creates Ship
     * @param location Location on map
     * @param maxVelocity maximum velocity
     */
    public Ship(Point2D location, double maxVelocity){
        super(location, maxVelocity);
        World.getInstance().addToShipList(this);
    }

}
