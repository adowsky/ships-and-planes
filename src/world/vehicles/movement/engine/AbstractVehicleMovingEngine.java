package world.vehicles.movement.engine;

import javafx.scene.shape.Shape;
import world.tools.SynchronizedUpdateNotifier;
import world.vehicles.Vehicle;
import world.vehicles.commons.DestroyListener;
import world.vehicles.commons.SerializableObject;
import world.vehicles.movement.Cross;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public abstract class AbstractVehicleMovingEngine implements MovingEngine<List<Cross>>, Serializable, DestroyListener {

    private static final String THREAD_NAME = "Vehicle Thread ";
    private Vehicle vehicle;
    private boolean canMove;
    private volatile List<Cross> route;
    private final SerializableObject routKeeper;
    private final SerializableObject movingGate;
    private transient boolean running;
    private boolean onCrossing;
    private Vehicle vehicleInFront;
    private Cross current;
    private transient Thread thread;

    AbstractVehicleMovingEngine(Vehicle vehicle) {
        this.vehicle = vehicle;
        vehicleInFront = null;
        this.movingGate = new SerializableObject();
        routKeeper = new SerializableObject();
        SynchronizedUpdateNotifier.INSTANCE.addToList(this);
        running = false;
        onCrossing = false;
    }

    @Override
    public void clearCanMove() {
        synchronized (movingGate) {
            canMove = false;
        }
    }

    @Override
    public void hitTheRoad(List<Cross> route) {
        synchronized (routKeeper) {
            this.route = route;
        }

        synchronized (this) {
            if (!running) {
                SynchronizedUpdateNotifier.INSTANCE.addToList(this);
                running = true;
                if (thread != null)
                    thread.interrupt();
                thread = new Thread(this, THREAD_NAME + vehicle.toString());
                thread.setDaemon(true);
                thread.start();
            }
        }
        setCanMove();
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public synchronized void destroy() {
        SynchronizedUpdateNotifier.INSTANCE.removeFromList(this);
        stop();
    }

    /**
     * Return state of flag can Move.
     *
     * @return flag canMove
     */
    public boolean canMove() {
        synchronized (movingGate) {
            return canMove;
        }
    }

    /**
     * Checks if current vehicle doesn't intersect with vehicle in front.
     *
     * @return if current vehicle doesn't intersect with vehicle in front.
     */
    private boolean shipIntersects() {
        if (vehicleInFront == null)
            return false;

        Shape s = Shape.intersect(vehicleInFront.getBounds(), vehicle.getBounds());
        return s.getBoundsInLocal().getWidth() >= 0;
    }

    @Override
    public void tick() {
        this.setCanMove();
    }

    @Override
    public void setCanMove() {
        synchronized (movingGate) {
            canMove = true;
        }
    }

    @Override
    public void runInThread() {
        while (!thread.isInterrupted()) {
            if (!vehicle.getReadyToTravel() || route.size() == 0) {
                trySleep();
            } else {
                if (current == null) {
                    if (!vehicle.isForcedRouteChange() && vehicle.getLastPort() != null) {
                        vehicle.getLastPort().goThrough(vehicle);
                    }
                    setFrontVehicle();
                    vehicle.moved();
                    for (Cross o : route) {
                        if (thread.isInterrupted())
                            break;
                        synchronized (this) {
                            current = o;
                        }
                        moveOnTheLine();
                        if (!isFinish()) {
                            onCrossing = true;
                            o.goThrough(vehicle);
                            onCrossing = false;
                        } else {
                            vehicle.nextCrossing();
                        }
                        setFrontVehicle();
                    }
                    synchronized (this) {
                        current = null;
                    }
                } else {
                    ListIterator<Cross> iter = route.listIterator();
                    Cross item = null;
                    if (vehicle.getNextCrossing() != current)
                        vehicle.decreaseCrossingIndex();
                    while (iter.hasNext() && item != current)
                        item = iter.next();
                    if (onCrossing) {
                        if (current == vehicle.getLastPort()) {
                            vehicle.decreasePortIndex();
                            vehicle.setDecreased(true);
                        }
                        current.goThrough(vehicle);
                        onCrossing = false;
                    } else
                        iter.previous();
                    while (iter.hasNext()) {
                        if (thread.isInterrupted())
                            break;
                        synchronized (this) {
                            current = iter.next();
                        }
                        moveOnTheLine();
                        if (!vehicle.isOnRouteFinish()) {
                            onCrossing = true;
                            current.goThrough(vehicle);
                            onCrossing = false;
                        } else {
                            vehicle.nextCrossing();
                        }
                        setFrontVehicle();

                    }
                    synchronized (this) {
                        current = null;
                    }
                }
            }
        }
    }

    private void moveOnTheLine() {
        while (!current.intersect(vehicle.getBounds())) {
            if (canMove() && !shipIntersects()) {
                vehicle.move();
                synchronized (this) {
                    canMove = false;
                }
            } else {
                trySleep();
            }
        }
    }

    private void setFrontVehicle() {
        if (vehicleInFront != null)
            vehicleInFront.removeDestroyListener(this);
        List<Vehicle> list = vehicle.getCurrentCrossing().getVehiclesTravellingTo(vehicle.getNextCrossing());
        if (list == null) {
            vehicleInFront = null;
            return;
        }
        Vehicle previous = null;
        for (Vehicle o : list) {
            if (o == vehicle) {
                vehicleInFront = previous;
                break;
            } else {
                previous = o;
            }
        }
        if (vehicleInFront != null)
            vehicleInFront.addDestroyListener(this);
    }

    /**
     * Sets current crossing.
     *
     * @param x current crossing.
     */
    public synchronized void setCurrent(Cross x) {
        current = x;
    }

    /**
     * Returns if vehicle is finishing cycle.
     *
     * @return if vehicle is finishing cycle.
     */
    public abstract boolean isFinish();

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        running = false;
    }

    @Override
    public void objectDestroyed(Vehicle o) {
        vehicleInFront = null;
    }
}
