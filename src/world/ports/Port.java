package world.ports;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import world.*;
import world.vehicles.MovingEngine;
import world.vehicles.PortMovingEngine;
import world.vehicles.Vehicle;

import java.util.*;

/**
 * Represents port. Vehicle can stay there.
 */
public abstract class Port implements Drawable, Cross {
    private int maxCapacity;
    private Circle circle;
    private Point2D location;
    private Set<Port> landConnectionPorts;
    private MovingEngine<Vehicle> engine;
    private Map<Port,List<Cross>> ways;
    private String name;
    private Map<Cross,List<Vehicle>> travellingMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }


    public Port(int maxCapacity, Point2D location) {
        this.maxCapacity = maxCapacity;
        landConnectionPorts = new HashSet<>();
        this.location = location;
        circle = new Circle(location.getX(), location.getY(), WorldConstants.PORT_RADIUS);
        engine = new PortMovingEngine(this);
        travellingMap = new HashMap<>();

    }
    public List<Cross> getRouteToPort(Port port){
        return ways.get(port);
    }
    public void setWays(Map<Port,List<Cross>> ways){
        this.ways = ways;
    }
    public Set<Port> getLandConnectionPorts() {
        return landConnectionPorts;
    }

    @Override
    public double getX() {
        return circle.getCenterX();
    }

    @Override
    public double getY() {
        return circle.getCenterY();
    }
    public Point2D getLocation(){
        return  location;
    }

    @Override
    public boolean intersect(Shape bounds) {
        return circle.intersects(bounds.getBoundsInLocal());
    }

    @Override
    public void goThrough(Vehicle c){
        engine.hitTheRoad(c);
    }


    /**
     * Returns if Port have maximum value of vehicles.
     *
     * @return true - Port have max value of vehicles, false - Port still have some space to land.
     */
    public abstract boolean isFull();
    @Override
    public List<Vehicle> getVehiclesTravellingTo(Cross name) {
        return travellingMap.get(name);
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
    public void removeFromTravellingTo(Cross name, Vehicle v){
        List<Vehicle> vehicleList = travellingMap.get(name);
        if(vehicleList == null){
            return;
        }
        vehicleList.remove(v);
    }
    @Override
    public String toString(){
        return getClass().getSimpleName()+": "+name;
    }


}
