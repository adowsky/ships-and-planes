package world.vehicles.commons;

import world.vehicles.movement.MovingState;

/**
 * Listener of Movement state change.
 */
public interface MovementStateListener {
    /**
     * Process change of movement state.
     * @param state new movement state.
     */
    void movementStateChanges(MovingState state);
}
