package world.vehicles.movement;

import javafx.scene.shape.Shape;
import world.Cross;
import world.vehicles.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;

import java.util.List;

/**
 * Moving Engine which doesn't allow vehicle to go into Port.
 */
public class DodgingPortsMovingEngine implements MovingEngine<List<Cross>> {
        private Vehicle vehicle;
        private boolean canMove;
        private  volatile List<Cross> route;
        private final Object routKeeper;
        private final Object movingGate;
        private Vehicle vehicleInFront;

        /**
         * Creates new DodgingPortsMovingEngine
         * @param vehicle vehicle of engine
         */
        public DodgingPortsMovingEngine(Vehicle vehicle){
            this.vehicle = vehicle;
            vehicleInFront = null;
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
                        if(!vehicle.isOnRouteFinish())
                            o.goThrough(vehicle);
                        else{
                            vehicle.nextCrossing();
                        }
                        setFrontVehicle();
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


}
