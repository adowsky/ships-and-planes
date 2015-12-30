package world.vehicles.movement;

import javafx.scene.shape.Shape;
import world.Cross;
import world.vehicles.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
