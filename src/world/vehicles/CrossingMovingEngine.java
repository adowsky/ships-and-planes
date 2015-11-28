package world.vehicles;

import javafx.geometry.Point2D;
import world.Crossing;

/**
 * Moving engine to move vehicle through crossing.
 */
public class CrossingMovingEngine implements MovingEngine<Vehicle> {
    private Crossing cross;
    private volatile boolean canMove;
    private Vehicle c;
    private Object monitor;

    public CrossingMovingEngine(Crossing cross){
        this.cross = cross;
        canMove = false;
        monitor = new Object();
    }
    @Override
    public synchronized void hitTheRoad(Vehicle c) {
        this.c = c;
        runInThread();
    }

    @Override
    public  void setCanMove() {
        synchronized (monitor) {
            canMove = true;
        }
    }

    @Override
    public synchronized void runInThread() {
        if(c == null)
            return;
        toTheCenter(c);
        c.nextCrossing();
        while(cross.intersect(c.getBounds())){
            move(c);
        }
    }

    private boolean canMove(){
        synchronized (monitor) {
            return canMove;
        }
    }
    private synchronized void move(Vehicle c) {
        if (canMove()) {
            synchronized (this) {
                c.move();
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
    private void toTheCenter(Vehicle c){
        double x;
        double y;
        if(c.getSpeedX()<0)
            x = cross.getX()+1;
        else
            x = cross.getX() - 1;
        if(c.getSpeedY()<0)
            y = cross.getY() + 1;
        else
            y = cross.getY() - 1;
        Point2D p = new Point2D(x, y);
        while(!c.getBounds().contains(p)){
            move(c);
        }

    }

    }

