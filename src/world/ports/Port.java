package world.ports;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import world.*;
import world.vehicles.MovingEngine;
import world.vehicles.PortMovingEngine;
import world.vehicles.Vehicle;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents port. Vehicle can stay there.
 */
public abstract class Port implements Drawable, Cross {
    private int maxCapacity;
    private Circle circle;
    private Set<Port> landConnectionPorts;
    private MovingEngine<Vehicle> engine;
    private Map<Port,List<Cross>> ways;

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Port(int maxCapacity, Point2D location,Map<Port,List<Cross>> ways) {
        this.maxCapacity = maxCapacity;
        landConnectionPorts = new HashSet<>();
        circle = new Circle(location.getX(), location.getY(), WorldConstants.PORT_RADIUS);
        engine = new PortMovingEngine(this);
        this.ways = ways;

    }
    public List<Cross> getRouteToPort(Port port){
        return ways.get(port);
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

    @Override
    public boolean intersect(Bounds bounds) {
        return circle.intersects(bounds);
    }

    @Override
    public void goThrough(Vehicle c){
        engine.hitTheRoad(c);
    }

    /**
     * Returns if port is military
     *
     * @return true if port is military, false if not
     */
    public abstract boolean isMilitary();

    /**
     * Returns if Port have maximum value of vehicles.
     *
     * @return true - Port have max value of vehicles, false - Port still have some space to land.
     */
    public abstract boolean isFull();

    /**
     * Returns if it is airPort
     *
     * @return true - it is airport, false -it is not
     */
    public abstract boolean isAir();

}
