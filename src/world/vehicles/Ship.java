package world.vehicles;

import javafx.geometry.Point2D;
import world.vehicles.movement.MovingEngineTypes;

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
    }
    public Ship(Point2D location, double maxVelocity, MovingEngineTypes type){
        super(location, maxVelocity, type);
    }

}
