package world.vehicles.movement.engine;

import world.vehicles.Vehicle;
import world.vehicles.movement.Cross;

import java.util.List;

/**
 * Class that creates VehicleMovingEngines depends on type.
 */
public enum VehicleMovingEngineFactory {
    INSTANCE;

    /**
     * Returns new Instance of specified moving engine.
     * @param type type of moving engine
     * @param v vehicle of moving engine.
     * @return  new Instance of specified moving engine.
     */
    public MovingEngine<List<Cross>> getMovingEngine(MovingEngineType type, Vehicle v){
        MovingEngine<List<Cross>> engine = null;
        switch (type){
            case STANDARD:
                return new VehicleMovingEngine(v);

            case DODGING_PORTS:
                return new DodgingPortsMovingEngine(v);

            default:
                break;
        }
        return engine;
    }
}
