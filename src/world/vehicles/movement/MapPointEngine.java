package world.vehicles.movement;

import world.vehicles.Notifiable;
import world.vehicles.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;

import java.io.IOException;

/**
 * Template of moving engine for Cross.
 */
public abstract class MapPointEngine implements MovingEngine<Vehicle>, Notifiable {
    private static long serialVersionUID = 1L;
    private transient Object monitor;
    private boolean canMove;
    private Cross cross;
    private Vehicle c;

    /**
     * Creates instance of class.
     * @param cross
     */
    public MapPointEngine(Cross cross){
        monitor = new Object();
        this.cross = cross;
        SynchronizedUpdateNotifier.INSTANCE.addToList(this);
    }

    @Override
    public synchronized void hitTheRoad(Vehicle c) {
        this.c = c;
        runInThread();
    }

    /**
     * moves object out of the cross
     */
    public void moveOut(){
        while(cross.intersect(c.getBounds()) && !Thread.currentThread().isInterrupted()){
            move(c);
        }
    }

    @Override
    public synchronized void runInThread() {
        if(c == null)
            return;
        toTheCenter();
        c.nextCrossing();
    }
    protected Vehicle getActiveVehicle(){
        return c;
    }
    @Override
    public void clearCanMove(){
        synchronized (monitor){
            canMove = false;
        }
    }

    /**
     * returns if object can move
     * @return if object can move
     */
    public boolean canMove(){
        synchronized (monitor) {
            return canMove;
        }
    }

    /**
     * Moves object to the center of the cross.
     */
    public void toTheCenter(){
        while(!checkStopCondition(c) && Thread.currentThread().isInterrupted()){
            move(c);
        }
    }

    /**
     * Checks if object reaches center of the cross.
     * @param c moving object
     * @return  if object reaches center of the cross.
     */
    public boolean checkStopCondition(Vehicle c){
        boolean vertical;
        if(c.getSpeedY()<0){
            vertical = c.getLocation().getY() < cross.getY();
        }else{
            vertical = c.getLocation().getY() > cross.getY();
        }
        boolean horizontal;
        if(c.getSpeedX()<0){
            horizontal = c.getLocation().getX() < cross.getX();
        }else{
            horizontal = c.getLocation().getX() > cross.getX();
        }
        return horizontal || vertical;
    }
    @Override
    public  void setCanMove() {
        synchronized (monitor) {
            canMove = true;
        }
}
    @Override
    public void tick(){
        setCanMove();
    }
    @Override
    public void stop(){}
    @Override
    public void destroy(){}

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        monitor = new Object();
        SynchronizedUpdateNotifier.INSTANCE.addToList(this);
    }
}
