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

    /**
     * Returns instance of class
     * @return instance of class
     */
    public synchronized static VehicleMovingEngineFactory getInstance(){
        if(instance == null)
            instance = new VehicleMovingEngineFactory();
        return instance;
    }

    /**
     * Returns new Instance of specified moving engine.
     * @param type type of moving engine
     * @param v vehicle of moving engine.
     * @return  new Instance of specified moving engine.
     */
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
