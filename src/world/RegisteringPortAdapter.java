package world;

import javafx.geometry.Point2D;
import world.ports.Port;
import world.vehicles.Vehicle;

import java.util.*;

/**
 * Adapter imitating cross to register vehicles.
 */
public class RegisteringPortAdapter extends Port {
    public RegisteringPortAdapter(Point2D loc){
        super(1, loc);
    }
    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public Collection<Passenger> getPassengers() {
        return null;
    }

    @Override
    public Collection<? extends Vehicle> getVehicles() {
        return null;
    }

    @Override
    public void objectDestroyed(Vehicle o) {

    }

    @Override
    public void Draw() {

    }
}
