package world;

import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;
import world.vehicles.Vehicle;

import java.util.List;

/**
 * Crossing point.
 */
public interface Cross {
    void goThrough(Vehicle vehicle);
    boolean intersect(Shape bounds);
    double getX();
    double getY();
    String getName();
    void setName(String name);
    List<Vehicle> getVehiclesTravellingTo(Cross name);
    void registerNewTravellingTo(Cross name, Vehicle v);
    public void removeFromTravellingTo(Cross name, Vehicle v);

}
