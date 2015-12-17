package world.vehicles;

import javafx.scene.shape.Shape;
import world.Cross;
import world.World;

import java.util.List;

/**
 * It's responsible for vehicle's moving;
 */
public class VehicleMovingEngine implements MovingEngine<List<Cross>> {
    private Vehicle vehicle;
    private boolean canMove;
    private  volatile List<Cross> route;
    private final Object routKeeper;
    private final Object movingGate;
    private Vehicle vehicleInFront;
    public VehicleMovingEngine(Vehicle vehicle){
        this.vehicle = vehicle;
        vehicleInFront = null;
        this.movingGate = new Object();
        SynchronizedUpdateNotifier.getInstance().addToList(this);
        routKeeper = new Object();
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();

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
        setCanMove();
    }
    @Override
    public void runInThread(){
        while(true) {
            if(!vehicle.getReadyToTravel()){
                trySleep();
            }
            else {
                for (Cross o : route) {
                    while (!o.intersect(vehicle.getBounds())) {
                        if (canMove() && !shipIntersects()) {
                            synchronized (this) {
                                vehicle.move();
                                canMove = false;
                            }
                        } else {
                           trySleep();
                        }
                    }
                    o.goThrough(vehicle);
                    setFrontVehicle();
                }
            }
        }
    }
    @Override
    public void setCanMove() {
        synchronized (movingGate) {
            canMove = true;
        }
    }
    public  boolean canMove(){
        synchronized (movingGate) {
            return canMove;
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
    public boolean shipIntersects(){
        if(vehicleInFront == null)
            return false;
        Shape s = Shape.intersect(vehicleInFront.getBounds(),vehicle.getBounds());
        return s.getBoundsInLocal().getWidth()<0 ? false : true;
    }

    @Override
    public void tick() {
        this.setCanMove();
    }
}
