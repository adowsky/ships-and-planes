package world.vehicles.movement;

import world.vehicles.Vehicle;

/**
 * It's responsible for vehicle's moving;
 */
public class VehicleMovingEngine extends AbstractVehicleMovingEngine {
    /**
     * Creates new VehicleMovingEngine
     * @param vehicle vehicle of engine
     */
    public VehicleMovingEngine(Vehicle vehicle){
        super(vehicle);
    }

    @Override
    public boolean isFinish() {
        return false;
    }

}
