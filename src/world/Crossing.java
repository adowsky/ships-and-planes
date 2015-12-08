package world;

import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import world.vehicles.CrossingMovingEngine;
import world.vehicles.MovingEngine;
import world.vehicles.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents crossing.
 */
public class Crossing implements Cross{
    private Circle circle;
    private volatile MovingEngine<Vehicle> engine;
    private String name;
    private Map<Cross, List<Vehicle>> travellingMap;

    public Crossing(double x, double y, int radius){
        circle = new Circle(x,y,radius);
        name = "Crossing name";
        engine = new CrossingMovingEngine(this);
        SynchronizedUpdateNotifier.getInstance().addToList(engine);
        travellingMap = new HashMap<>();
    }
    public boolean intersect(Shape bounds){
        return circle.intersects(bounds.getBoundsInLocal());
    }
    public synchronized void goThrough(Vehicle vehicle){
        engine.hitTheRoad(vehicle);
    }
    public double  getX(){
        return circle.getCenterX();
    }
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


}
