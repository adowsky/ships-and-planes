package world.vehicles.movement.engine;
import world.vehicles.Vehicle;

/**
 * Moving Engine which doesn't allow vehicle to go into Port.
 */
public class DodgingPortsMovingEngine extends AbstractVehicleMovingEngine {
    private static long serialVersionUID = 1L;
    private Vehicle vehicle;
        /**
         * Creates new DodgingPortsMovingEngine
         * @param vehicle vehicle of engine
         */
        public DodgingPortsMovingEngine(Vehicle vehicle){
            super(vehicle);
            this.vehicle = vehicle;
        }


        @Override
        public boolean isFinish(){
            return vehicle.isOnRouteFinish();
        }
}
