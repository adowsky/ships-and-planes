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
    private Object routKeeper;
    public VehicleMovingEngine(Vehicle vehicle){
        this.vehicle = vehicle;
        SynchronizedUpdateNotifier.getInstance().addToList(this);
        routKeeper = new Object();
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();

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
                        if (canMove()) {
                            synchronized (this) {
                                vehicle.setLocation(vehicle.getLocation().getX() + vehicle.getSpeedX(),
                                        vehicle.getLocation().getY() + vehicle.getSpeedY());
                                canMove = false;
                            }
                        } else {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    o.goThrough(vehicle);
                }
            }
        }
    }
    @Override
    public synchronized void setCanMove() {
        canMove = true;
    }
    private synchronized boolean canMove(){
        return canMove;
    }
}
