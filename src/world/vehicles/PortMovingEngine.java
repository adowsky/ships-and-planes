package world.vehicles;

import javafx.geometry.Point2D;
import world.Cross;
import world.ports.Port;

import java.util.List;
import java.util.Objects;

/**
 * Moves vehicle to the Port.
 */
public class PortMovingEngine implements MovingEngine<Vehicle> {
    private Port port;
    private volatile boolean canMove;
    private Object movingGuard;
    private Vehicle c ;
    public PortMovingEngine(Port port){
        this.port = port;
        canMove = false;
        movingGuard = new Object();
        SynchronizedUpdateNotifier.getInstance().addToList(this);
    }
    @Override
    public void hitTheRoad(Vehicle c) {
        this.c = c;
        runInThread();
    }

    @Override
    public void setCanMove() {
        synchronized (movingGuard) {
            canMove = true;
        }
    }

    @Override
    public synchronized void runInThread() {
        double x;
        double y;
        if(c.getSpeedX()<0)
            x = port.getX()+1;
        else
            x = port.getX() - 1;
        if(c.getSpeedY()<0)
            y = port.getY() - 1;
        else
            y = port.getY() + 1;
        Point2D p = new Point2D(x, y);
        if(c ==  null)
            return;
        while(! c.getBounds().contains(p)) {
            if (canMove()) {
                c.move();
                synchronized (movingGuard) {
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
        c.nextCrossing();
    }

    public boolean canMove(){
        synchronized (movingGuard) {
            return canMove;
        }
    }
}
