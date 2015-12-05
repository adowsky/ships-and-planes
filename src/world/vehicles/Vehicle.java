package world.vehicles;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import world.*;
import world.ports.Port;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents vehicle
 */
public abstract class Vehicle implements Drawable {
    private  volatile Point2D location;
    private final int id;
    private static int nextId=0;
    private int nextPortIndex;
    private boolean running;
    private boolean readyToTravel;
    private MovingEngine<List<Cross>> engine;
    private volatile Bounds bounds;
    boolean locationChanged;
    private double speed;
    private double speedX;
    private  double speedY;
    private List<Cross> route;
    private int nextCrossing;
    private List<LocationChangedListener> listeners;

    /**
     * Creates Vehicle with specific location and route
     * @param location location on map

     */
    public Vehicle(Point2D location, double speed){
        id=nextId++;
        this.location=location;
        readyToTravel = false;
        running = false;
        this.speed = speed;
        engine = new VehicleMovingEngine(this);
        bounds = new BoundingBox(location.getX(),location.getY(), WorldConstants.VEHICLE_WIDTH,WorldConstants.VEHICLE_HEIGHT);
        locationChanged = false;
        nextCrossing = 0;
        speedX = 0;
        speedY = 0;
        listeners = new LinkedList<>();

    }

    public synchronized Bounds getBounds() {
        if(locationChanged) {
            bounds = new BoundingBox(location.getX(), location.getY(), WorldConstants.VEHICLE_WIDTH, WorldConstants.VEHICLE_HEIGHT);
            locationChanged = false;
        }
        return bounds;
    }

    public double getSpeed() {
        return speed;
    }



    /**
     * Creates vehicle
     */
    @Deprecated
    public Vehicle(){
        id=nextId++;
    }

    /**
     * Returns location of vehicle
     * @return location of vehicle
     */
    public Point2D getLocation() {
        return location;
    }

    public synchronized void setLocation(double x, double y){
        location = new Point2D(x,y);
        locationChanged = true;
        for(LocationChangedListener l : listeners)
            l.fire(location);
    }
    public void addLocationChangedListener(LocationChangedListener l){
        listeners.add(l);
    }

    /**
     * Returns Id number of vehicle
     * @return Id number of vehicle
     */
    public int getId() {
        return id;
    }

    public abstract Port getNextPort();

    /**
     * Returns next Port
     * @return next port
     */
    public int getNextPortIndex() {
        return nextPortIndex;
    }

    /**
     * Sets new next port
     * @param nextPortIndex new next port
     */
    public void setNextPortIndex(int nextPortIndex) {
        this.nextPortIndex = nextPortIndex;
    }

    /**
     * Sleeps thread for specific time.
     * @param sleepTime time vehicle will sleep.
     */
    public void maintenanceStart(int sleepTime){
        try {
            Thread.sleep(sleepTime);
        }catch (InterruptedException e){
            e.printStackTrace();
            System.out.println("Vehicle sleep issue.");
        }
        finally {
            //TODO
        }
    }

    /**
     * Sets readyToTravel flag on true value.
     */
    public void setReadyToTravel(){
        readyToTravel = true;
        engine.hitTheRoad(route);
    }
    /**
     * Sets readyToTravel flag on false value.
     */
    public void arrivedToPort(){
        readyToTravel = false;
    }

    /**
     * Runs the thread.
     */
    public void sleep(int time){
        try{
            Thread.sleep(time);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public boolean isRunning(){
        return running;
    }
    public synchronized void move(double x, double y){
        setLocation(location.getX() + x, location.getY() + y);
    }

    public synchronized void move(){
        move(speedX,speedY);
    }
    public Cross getNextCrossing(){
        return route.get(nextCrossing);
    }

    public boolean isOnRouteFinish(){
        return nextCrossing+1 >=route.size();
    }
    public synchronized void nextCrossing(){
        //TODO instancyjne klasy muszą nadpisać o uruchomienie lądowania
        if(nextCrossing == route.size()){
            return;
        }
        nextCrossing++;
        Point2D p = countSpeed();
        speedX = p.getX();
        speedY = p.getY();
    }

    public void setRoute(List<Cross> l){
        route = l;
        nextCrossing = 0;
        Point2D p = countSpeed();
        speedX = p.getX();
        speedY = p.getY();
        System.out.println(this.getClass().getName()+"-"+this.getId()+": new Route loaded: "+route.size()+" entries.");
    }

    public double getSpeedY() {
        return speedY;
    }

    public double getSpeedX() {
        return speedX;
    }
    private Point2D countSpeed(){
        Cross crossing = getNextCrossing();
        int destX;
        int destY;
        if (crossing == null){
            destX = (int) getDestination().getX();
            destY = (int) getDestination().getY();
        }
        else{
            destX = (int)getNextCrossing().getX();
            destY = (int)getNextCrossing().getY();
        }
        double lengthX = Math.abs(destX - getLocation().getX());
        double lengthY = Math.abs(destY - getLocation().getY());
        double length = Math.sqrt(lengthX*lengthX + lengthY*lengthY);
        double sin = lengthY/length;
        double cos = lengthX/length;
        double speedX = getSpeed()*cos;
        double speedY = getSpeed()*sin;
        if(destX<location.getX())
            speedX = -speedX;
        if(destY < location.getY())
            speedY = -speedY;
        return new Point2D(speedX,speedY);
    }
    public abstract Port getDestination();
    public boolean getReadyToTravel(){
        return readyToTravel;
    }
    public abstract Map<String, String> getProperties();

}
