package world;

import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;
import world.vehicles.Vehicle;

import java.io.Serializable;
import java.util.List;

/**
 * Crossing point.
 */
public interface Cross extends Serializable{
    /**
     * Moves vehicle through cross
     * @param vehicle vehicle to move.
     */
    void goThrough(Vehicle vehicle);

    /**
     * checks if shape intersects with cross bounds.
     * @param bounds shape to check.
     * @return if shape intersects with cross bounds.
     */
    boolean intersect(Shape bounds);

    /**
     * Returns X coordinate of Cross.
     * @return X coordinate of Cross.
     */
    double getX();
    /**
     * Returns Y coordinate of Cross.
     * @return Y coordinate of Cross.
     */
    double getY();

    /**
     * Returns name of cross.
     * @return name of cross.
     */
    String getName();

    /**
     * Sets name of cross.
     * @param name name of cross.
     */
    void setName(String name);

    /**
     * Returns list of vehicles travelling on specific path.
     * @param name cross which specifies path
     * @return list of vehicles travelling on specific path.
     */
    List<Vehicle> getVehiclesTravellingTo(Cross name);

    /**
     * Saves vehicle in list of vehicles travelling on specific path.
     * @param name cross which specifies path
     * @param v vehicle to save
     */
    void registerNewTravellingTo(Cross name, Vehicle v);

    /**
     * Removes vehicle from list of vehicles travelling on specific path.
     * @param name  cross which specifies path
     * @param v vehicle to remove
     */
    void removeFromTravellingTo(Cross name, Vehicle v);

}
