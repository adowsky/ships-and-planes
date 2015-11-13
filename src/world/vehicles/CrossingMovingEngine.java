package world.vehicles;

import javafx.geometry.Point2D;
import world.Crossing;

/**
 * Move engine to move vehicle through crossing.
 */
public class CrossingMovingEngine implements MovingEngine<Vehicle> {
    private Crossing cross;
    private boolean canMove;

    public CrossingMovingEngine(Crossing cross){
        this.cross = cross;
        canMove = false;
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }
    @Override
    public synchronized void hitTheRoad(Vehicle c) {
        toTheCenter(c);
        c.nextCrossing();
        while(cross.intersect(c.getBounds())){
            move(c);
        }
    }

    @Override
    public synchronized void setCanMove() {
        canMove = true;
    }
    private synchronized boolean canMove(){
        return canMove;
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
        Point2D p = new Point2D(cross.getX(),cross.getY());
        while(!c.getBounds().contains(p)){
            move(c);
        }
    }

    }

