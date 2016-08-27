package world.vehicles.types.water;

import javafx.geometry.Point2D;
import world.vehicles.Vehicle;
import world.vehicles.movement.engine.MovingEngineType;

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
    public Ship(Point2D location, double maxVelocity, MovingEngineType type){
        super(location, maxVelocity, type);
    }

}
