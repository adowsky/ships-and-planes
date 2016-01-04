package world.ports;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import world.*;
import world.vehicles.Circle;
import world.vehicles.DestroyListener;
import world.vehicles.movement.MovingEngine;
import world.vehicles.movement.PortMovingEngine;
import world.vehicles.Vehicle;

import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Represents port. Vehicle can stay there.
 */
public abstract class Port implements Drawable, Cross, Serializable, DestroyListener {
    private static long serialVersionUID = 1L;
    private int maxCapacity;
    private Circle circle;
    private Point location;
    private Set<Port> landConnectionPorts;
    private MovingEngine<Vehicle> engine;
    private Map<Port,List<Cross>> ways;
    private String name;
    private Map<Cross,List<Vehicle>> travellingMap;

    /**
     * Returns name of the port.
     * @return name of the port.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the port.
     * @param name  name of the port.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns maximum capacity of the port.
     * @return  maximum capacity of the port.
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Creates new Port
     * @param maxCapacity Maximum capacity
     * @param location location point
     */
    public Port(int maxCapacity, Point2D location) {
        this.maxCapacity = maxCapacity;
        landConnectionPorts = new HashSet<>();
        this.location = new Point(location.getX(),location.getY());
        circle = new Circle(location.getX(), location.getY(), WorldConstants.PORT_RADIUS);
        engine = new PortMovingEngine(this);
        travellingMap = new HashMap<>();
        SerializeContainer.getInstance().addObjectToSerialize(this);

    }

    /**
     * Returns route(List of Crosses) to specific port.
     * @param port
     * @return  route(List of Crosses) to specific port.
     */
    public List<Cross> getRouteToPort(Port port){
        return ways.get(port);
    }

    /**
     * Sets map of routes to specific ports.
     * @param ways map.
     */
    public void setWays(Map<Port,List<Cross>> ways){
        this.ways = ways;
    }

    /**
     * Returns set of ports that connected with current port by land.
     * @return
     */
    public Set<Port> getLandConnectionPorts() {
        return landConnectionPorts;
    }

    /**
     * Sets land Connected Ports collection.
     * @param lcp land Connected Ports collection.
     */
    public void setLandConnectionPorts(Set<Port> lcp){
        landConnectionPorts = lcp;
    }

    @Override
    public double getX() {
        return circle.getCenterX();
    }

    @Override
    public double getY() {
        return circle.getCenterY();
    }

    /**
     * Returns location of the port.
     * @return location of the port.
     */
    public Point2D getLocation(){
        return new Point2D(location.getX(),location.getY());
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

    /**
     * Returns routes to other ports.
     * @return routes to other ports.
     */
    public Set<Port> getAllRoutes(){
        return ways.keySet();
    }

    /**
     * Returns collection of passengers in port.
     * @return collection of passengers in port.
     */
    public abstract Collection<Passenger> getPassengers();

    /**
     * Returns collection fo vehicles in Port
     * @return collection fo vehicles in Port
     */
    public abstract Collection<? extends Vehicle> getVehicles();

}
