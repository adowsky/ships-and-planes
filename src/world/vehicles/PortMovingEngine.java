package world.vehicles;

import javafx.geometry.Point2D;
import world.ports.Port;

/**
 * Created by ado on 13/11/15.
 */
public class PortMovingEngine implements MovingEngine<Vehicle> {
    private Port port;
    private boolean canMove;
    public PortMovingEngine(Port port){
        this.port = port;
        canMove = true;
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }
    @Override
    public void hitTheRoad(Vehicle c) {
        Point2D p = new Point2D(port.getX(),port.getY());
        while(! c.getBounds().contains(p)) {
            if (canMove()) {
                c.move();
                canMove = false;

            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        c.nextCrossing();
    }

    @Override
    public synchronized void setCanMove() {
        canMove = true;
    }
    public synchronized boolean canMove(){
        return canMove;
    }
}
