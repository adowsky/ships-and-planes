package world.vehicles;

import world.Cross;

import java.util.List;

/**
 * It's responsible for vehicle's moving;
 */
public class VehicleMovingEngine implements MovingEngine<List<Cross>> {
    private Vehicle vehicle;
    private boolean canMove;

    public VehicleMovingEngine(Vehicle vehicle){
        this.vehicle = vehicle;
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();

    }

    @Override
    public void hitTheRoad(List<Cross> route) {
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

    @Override
    public synchronized void setCanMove() {
        canMove = true;
    }
    private synchronized boolean canMove(){
        return canMove;
    }
}
