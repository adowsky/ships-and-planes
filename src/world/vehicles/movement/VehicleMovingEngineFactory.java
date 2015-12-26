package world.vehicles.movement;

import world.Cross;
import world.vehicles.Vehicle;

import java.util.List;

/**
 * Class that creates VehicleMovingEngines depends on type.
 */
public class VehicleMovingEngineFactory {
    private static VehicleMovingEngineFactory instance;

    private VehicleMovingEngineFactory(){

    }
    public synchronized static VehicleMovingEngineFactory getInstance(){
        if(instance == null)
            instance = new VehicleMovingEngineFactory();
        return instance;
    }
    public MovingEngine<List<Cross>> getMovingEngine(MovingEngineTypes type, Vehicle v){
        MovingEngine<List<Cross>> me = null;
        switch (type){
            case STANDARD:
                return new VehicleMovingEngine(v);
            case DODGING_PORTS:
                return new DodgingPortsMovingEngine(v);
            default:
                break;
        }
        return me;
    }
}
