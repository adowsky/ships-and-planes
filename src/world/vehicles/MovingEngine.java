package world.vehicles;

/**
 * Responsible for moving something.
 * @param <T> Objects that needs moving service.
 */
public interface MovingEngine<T> extends Runnable, Notifiable {
    /**
     * Runs the whole sequence of movement engine.
     * @param c type of using element.
     */
    void hitTheRoad(T c);

    /**
     * Sets can move flag.
     */

    void setCanMove();

    /**
     * Runs movement in engine's thread.
     */

    void runInThread();

    /**
     * returns canMove flag.
     * @return can move flag.
     */
    boolean canMove();

    /**
     * Sets canMove flag on false.
     */
    void clearCanMove();

    @Override
    default void run() {
        while(true) {
            runInThread();
        }
    }

    /**
     * Sleeps thread for 1 ms.
     */
    default void trySleep(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Moves vehicle.
     * @param c vehicle to move.
     */
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
