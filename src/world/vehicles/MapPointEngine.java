package world.vehicles;

import world.Cross;

/**
 * Created by ado on 29/11/15.
 */
public abstract class MapPointEngine implements MovingEngine<Vehicle>, Notifiable{
    private final Object monitor;
    private boolean canMove;
    private Cross cross;
    private Vehicle c;
    public MapPointEngine(Cross cross){
        monitor = new Object();
        this.cross = cross;
        SynchronizedUpdateNotifier.getInstance().addToList(this);
    }

    @Override
    public synchronized void hitTheRoad(Vehicle c) {
        this.c = c;
        runInThread();
    }

    public void moveOut(){
        while(cross.intersect(c.getBounds())){
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
    @Override
    public void clearCanMove(){
        synchronized (monitor){
            canMove = false;
        }
    }
    public boolean canMove(){
        synchronized (monitor) {
            return canMove;
        }
    }
    public void toTheCenter(){
        while(!checkStopCondition(c)){
            move(c);
        }
    }
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
}
