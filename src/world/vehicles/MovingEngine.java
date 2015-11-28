package world.vehicles;

/**
 * Responsible for moving T parameter.
 * @param <T> Objects that needs moving service.
 */
public interface MovingEngine<T> extends Runnable {
    void hitTheRoad(T c);

    void setCanMove();

    void runInThread();

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
}
