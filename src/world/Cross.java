package world;

import javafx.geometry.Bounds;
import world.vehicles.Vehicle;

/**
 * Crossing point.
 */
public interface Cross {
    void goThrough(Vehicle vehicle);
    boolean intersect(Bounds bounds);
    double getX();
    double getY();

}
