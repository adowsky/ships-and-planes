package world.vehicles;

import java.util.HashSet;
import java.util.Set;

/**
 * Container to store created Ship instances.
 */
public class ShipContainer {
    private static ShipContainer instance;
    private Set<Ship> set;
    private ShipContainer(){
        set = new HashSet<>();
    }
    public static ShipContainer getInstance(){
        synchronized (ShipContainer.class) {
            if (instance == null)
                instance = new ShipContainer();
        }
        return instance;
    }
    public void addShip(Ship o){
        set.add(o);
    }
    public Set<Ship> getShipSet(){
        return set;
    }
}
