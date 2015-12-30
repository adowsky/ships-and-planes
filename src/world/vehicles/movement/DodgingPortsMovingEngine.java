package world.vehicles.movement;
import world.vehicles.Vehicle;

/**
 * Moving Engine which doesn't allow vehicle to go into Port.
 */
public class DodgingPortsMovingEngine extends AbstractVehicleMovingEngine {
    private Vehicle vehicle;
        /**
         * Creates new DodgingPortsMovingEngine
         * @param vehicle vehicle of engine
         */
        public DodgingPortsMovingEngine(Vehicle vehicle){
            super(vehicle);
            this.vehicle = vehicle;
        }

        /**
         * Sets vehicle that is in front of current one.
         */

        public boolean isFinish(){
            return vehicle.isOnRouteFinish();
        }
}
