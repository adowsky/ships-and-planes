package world.vehicles.movement;

import javafx.scene.shape.Shape;
import world.Cross;
import world.vehicles.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Moving Engine which doesn't allow vehicle to go into Port.
 */
public class DodgingPortsMovingEngine implements MovingEngine<List<Cross>> {
        private static long serialVersionUID = 1L;
        private Vehicle vehicle;
        private boolean canMove;
        private  volatile List<Cross> route;
        private transient Object routKeeper;
        private transient Object movingGate;
        private transient boolean running;
        private boolean routeChanged;
        private Vehicle vehicleInFront;
        private Cross current;
        private transient Thread thread;

        /**
         * Creates new DodgingPortsMovingEngine
         * @param vehicle vehicle of engine
         */
        public DodgingPortsMovingEngine(Vehicle vehicle){
            this.vehicle = vehicle;
            vehicleInFront = null;
            this.movingGate = new Object();
            SynchronizedUpdateNotifier.INSTANCE.addToList(this);
            routKeeper = new Object();
            running = false;
        }
        @Override
        public void clearCanMove(){
            synchronized (movingGate){
                canMove = false;
            }
        }
        @Override
        public void hitTheRoad(List<Cross> route) {
            synchronized (this) {
                if (!running) {
                    running = true;
                    thread = new Thread(this);
                    thread.setDaemon(true);
                    thread.start();
                }
            }
            synchronized (routKeeper) {
                this.route = route;
                routeChanged=true;
            }
            setCanMove();
        }
        @Override
        public void runInThread(){
            while(true) {
                if(!vehicle.getReadyToTravel()){
                    trySleep();
                }
                else if(routeChanged) {
                    if (current == null) {
                        for (Cross o : route) {
                            current = o;
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
                            if (!vehicle.isOnRouteFinish())
                                o.goThrough(vehicle);
                            else {
                                routeChanged = false;
                                vehicle.nextCrossing();
                            }
                            setFrontVehicle();
                        }
                        current = null;
                    }else{
                        ListIterator<Cross> iter = route.listIterator();
                        Cross item = null;
                        while(iter.hasNext() && item!= current)
                            item = iter.next();
                        iter.previous();
                        while (iter.hasNext()){
                            current = iter.next();
                            while (!current.intersect(vehicle.getBounds())) {
                                if (canMove() && !shipIntersects()) {
                                    synchronized (this) {
                                        vehicle.move();
                                        canMove = false;
                                    }
                                } else {
                                    trySleep();
                                }
                            }
                            current.goThrough(vehicle);
                            setFrontVehicle();

                        }
                        current = null;
                    }
                }
            }
        }

    @Override
    public synchronized void stop() {
        SynchronizedUpdateNotifier.INSTANCE.removeFromList(this);
        running = false;
        thread.interrupt();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
        public void setCanMove() {
            synchronized (movingGate) {
                canMove = true;
            }
        }

        /**
         * Return state of flag can Move.
         * @return flag canMove
         */
        public  boolean canMove(){
            synchronized (movingGate) {
                return canMove;
            }
        }

        /**
         * Sets vehicle that is in front of current one.
         */
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

        /**
         * Checks if current vehicle doesn't intersect with vehicle in front.
         * @return if current vehicle doesn't intersect with vehicle in front.
         */
        public boolean shipIntersects(){
            if(vehicleInFront == null)
                return false;
            Shape s = Shape.intersect(vehicleInFront.getBounds(),vehicle.getBounds());
            return s.getBoundsInLocal().getWidth() >= 0;
        }

        @Override
        public void tick() {
            this.setCanMove();
        }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        routKeeper = new Object();
        movingGate = new Object();
        running = false;
    }


}
