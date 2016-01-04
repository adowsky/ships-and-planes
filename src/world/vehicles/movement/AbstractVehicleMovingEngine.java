package world.vehicles.movement;

import javafx.scene.shape.Shape;
import world.Cross;
import world.vehicles.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public abstract class AbstractVehicleMovingEngine implements MovingEngine<List<Cross>>, Serializable{
    private static long serialVersionUID = 1L;
    private Vehicle vehicle;
    private boolean canMove;
    private  volatile List<Cross> route;
    private transient Object routKeeper;
    private transient Object movingGate;
    private transient boolean running;

    private Vehicle vehicleInFront;
    private Cross current;
    private transient Thread thread;

    public AbstractVehicleMovingEngine(Vehicle vehicle){
        this.vehicle = vehicle;
        vehicleInFront = null;
        this.movingGate = new Object();
        routKeeper = new Object();
        SynchronizedUpdateNotifier.INSTANCE.addToList(this);
        System.out.println("Dodałę");
        running = false;
    }

    @Override
    public void clearCanMove(){
        synchronized (movingGate){
            canMove = false;
        }
    }
    @Override
    public void hitTheRoad(List<Cross> route) {
        synchronized (routKeeper) {
            this.route = route;
        }
        synchronized (this) {
            if (!running) {
                SynchronizedUpdateNotifier.INSTANCE.addToList(this);
                running = true;
                if(thread != null)
                    thread.stop();
                thread = new Thread(this);
                thread.setDaemon(true);
                thread.start();
            }
        }
        setCanMove();
    }

    @Override
    public synchronized void stop() {
        running = false;
        if(thread == null)
            return;
        thread.interrupt();
        if(thread.isAlive())
            thread.stop();
    }
    @Override
    public synchronized void destroy(){
        SynchronizedUpdateNotifier.INSTANCE.removeFromList(this);
        stop();
    }


    /**
     * Return state of flag can Move.
     * @return flag canMove
     */
    public  boolean canMove(){
        synchronized (movingGate) {
            return canMove;
        }
    }

    /**
     * Checks if current vehicle doesn't intersect with vehicle in front.
     * @return if current vehicle doesn't intersect with vehicle in front.
     */
    public boolean shipIntersects(){
        if(vehicleInFront == null)
            return false;
        Shape s = Shape.intersect(vehicleInFront.getBounds(),vehicle.getBounds());
        return s.getBoundsInLocal().getWidth() >= 0;
    }
    @Override
    public void tick() {
        this.setCanMove();
    }

    @Override
    public void setCanMove() {
        synchronized (movingGate) {
            canMove = true;
        }
    }
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        routKeeper = new Object();
        movingGate = new Object();
        running = false;
    }
    @Override
    public void runInThread(){
        while(!thread.isInterrupted()) {
            if(!vehicle.getReadyToTravel() || route.size() == 0){
                trySleep();
            }
            else{
                if (current == null) {
                    if(!vehicle.isForcedRouteChange() && vehicle.getLastPort()!= null)
                        vehicle.getLastPort().goThrough(vehicle);
                    setFrontVehicle();
                    vehicle.moved();
                    for (Cross o : route) {
                        if(thread.isInterrupted())
                            break;
                        synchronized (this) {
                            current = o;
                        }
                        moveOnTheLine();
                        if (!isFinish())
                            o.goThrough(vehicle);
                        else {
                            vehicle.nextCrossing();
                        }
                        setFrontVehicle();
                    }
                    synchronized (this) {
                        current = null;
                    }
                }else{
                    ListIterator<Cross> iter = route.listIterator();
                    Cross item = null;
                    while(iter.hasNext() && item!= current)
                        item = iter.next();
                    iter.previous();
                    while (iter.hasNext()){
                        if(thread.isInterrupted())
                            break;
                        synchronized (this) {
                            current = iter.next();
                        }
                        moveOnTheLine();
                        if (!vehicle.isOnRouteFinish())
                            current.goThrough(vehicle);
                        else {
                            vehicle.nextCrossing();
                        }
                        setFrontVehicle();

                    }
                    synchronized (this) {
                        current = null;
                    }
                }
            }
        }
    }
    private void moveOnTheLine(){
        while (!current.intersect(vehicle.getBounds())) {
            if (canMove() && !shipIntersects()) {
                synchronized (this) {
                    vehicle.move();
                    canMove = false;
                }
            } else {
                trySleep();
            }
        }
    }
    private void setFrontVehicle(){
        List<Vehicle> list =vehicle.getCurrentCrossing().getVehiclesTravellingTo(vehicle.getNextCrossing());
        if(list == null) {
            vehicleInFront = null;
            return;
        }
        Vehicle previous = null;
        for(Vehicle o : list){
            if(o == vehicle){
                vehicleInFront = previous;
                break;
            }else{
                previous = o;
            }
        }
    }

    /**
     * Sets current crossing.
     * @param x current crossing.
     */
    public synchronized void setCurrent(Cross x){
        current = x;
    }

    /**
     * Returns if vehicle is finishing cycle.
     * @return  if vehicle is finishing cycle.
     */
    public abstract boolean isFinish();
}
