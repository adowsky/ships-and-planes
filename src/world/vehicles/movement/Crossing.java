package world.vehicles.movement;

import javafx.scene.shape.Shape;
import world.Circle;
import world.vehicles.commons.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;
import world.vehicles.movement.engine.CrossingMovingEngine;
import world.vehicles.movement.engine.MovingEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents crossing.
 */
public class Crossing implements Cross{
    private static long serialVersionUID = 1L;
    private Circle circle;
    private volatile MovingEngine<Vehicle> engine;
    private String name;
    private Map<Cross, List<Vehicle>> travellingMap;

    /**
     * Creates new instance of class.
     * @param x X coordinate of location
     * @param y Y coordinate of location
     * @param radius radius value
     */
    public Crossing(double x, double y, int radius){
        circle = new Circle(x,y,radius);
        name = "Crossing: ";
        engine = new CrossingMovingEngine(this);
        SynchronizedUpdateNotifier.INSTANCE.addToList(engine);
        travellingMap = new HashMap<>();
    }
    @Override
    public boolean intersect(Shape bounds){
        return circle.intersects(bounds.getBoundsInLocal());
    }
    @Override
    public synchronized void goThrough(Vehicle vehicle){
        engine.hitTheRoad(vehicle);
    }
    @Override
    public double  getX(){
        return circle.getCenterX();
    }
    @Override
    public double getY(){
        return circle.getCenterY();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Vehicle> getVehiclesTravellingTo(Cross name) {
        return travellingMap.get(name);
    }
    @Override
    public void removeFromTravellingTo(Cross name, Vehicle v){
        List<Vehicle> vehicleList = travellingMap.get(name);
        if(vehicleList == null){
            return;
        }
        vehicleList.remove(v);
    }
    @Override
    public void registerNewTravellingTo(Cross name, Vehicle v){
        List<Vehicle> vehicleList = travellingMap.get(name);
        if(vehicleList == null){
            vehicleList = new ArrayList<>();
            travellingMap.put(name,vehicleList);
        }
        vehicleList.add(v);
    }
    @Override
    public String toString(){
        return "Crossing: "+name;
    }

}
