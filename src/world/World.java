package world;

import javafx.scene.shape.Shape;
import world.vehicles.Airplane;
import world.vehicles.Ship;
import world.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for World's instances.
 */
public class World {
    private static World instance;
    private List<Ship> shipList;
    private List<Airplane> planeList;
    private World(){
        shipList = new ArrayList<>();
    }
    public static World getInstance(){
        synchronized (World.class){
            if(instance == null)
                instance = new World();
        }
        return instance;
    }
    public List<Ship> getShipList(){
        return shipList;
    }
    public boolean shipIntersect(Vehicle vehicle){
        for(Ship o : shipList){
            Shape intersection = Shape.intersect(o.getBounds(),vehicle.getBounds());
            if(o!= vehicle && intersection.getBoundsInLocal().getWidth()!=-1)
                return true;
        }
        return false;
    }

    public List<Airplane> getPlaneList() {
        return planeList;
    }
    public void addToPlaneList(Airplane plane){
        planeList.add(plane);
    }
    public void addToShipList(Ship ship){
        shipList.add(ship);
    }
}
