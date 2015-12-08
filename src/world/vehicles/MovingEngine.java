package world.vehicles;

import world.World;

/**
 * Responsible for moving something.
 * @param <T> Objects that needs moving service.
 */
public interface MovingEngine<T> extends Runnable {
    void hitTheRoad(T c);

    void setCanMove();

    void runInThread();
    boolean canMove();
    void clearCanMove();

    @Override
    default void run() {
        while(true) {
            runInThread();
        }
    }
    default void trySleep(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    default void move(Vehicle c) {
        if (canMove()) {
            c.move();
            synchronized (this) {
                clearCanMove();
            }
        } else {
            trySleep();
        }
    }
}
