package world.vehicles;

import world.Cross;

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
    public VehicleMovingEngine(Vehicle vehicle){
        this.vehicle = vehicle;
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
                    System.out.println("Going to: "+ o.getClass().getName());
                    while (!o.intersect(vehicle.getBounds())) {
                        if (canMove()) {
                            synchronized (this) {
                                vehicle.move();
                                canMove = false;
                            }
                        } else {
                           trySleep();
                        }
                    }
                    o.goThrough(vehicle);
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
}
