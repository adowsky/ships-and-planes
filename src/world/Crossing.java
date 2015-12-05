package world;

import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;
import world.vehicles.CrossingMovingEngine;
import world.vehicles.MovingEngine;
import world.vehicles.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;

/**
 * Represents crossing.
 */
public class Crossing implements Cross{
    private Circle circle;
    private volatile MovingEngine<Vehicle> engine;

    public Crossing(double x, double y, int radius){
        circle = new Circle(x,y,radius);
        engine = new CrossingMovingEngine(this);
        SynchronizedUpdateNotifier.getInstance().addToList(engine);
    }
    public boolean intersect(Bounds bounds){
        return circle.intersects(bounds);
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
}
