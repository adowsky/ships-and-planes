package world;

import world.vehicles.Vehicle;

/**
 * Renderer for Vehicle.
 */
public class ConsoleVehicleView implements VehicleView {


    @Override
    public void paint(Vehicle v) {
        System.out.println("Vehicle Location: " + v.getLocation().toString());
    }
}
