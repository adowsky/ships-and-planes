package world.vehicles.commons;

import world.vehicles.Vehicle;

/**
 * Interface of Object destroy listener.
 */
public interface DestroyListener {
    /**
     * Process object destroy.
     * @param o destroyed object.
     */
    void objectDestroyed(Vehicle o);
}
