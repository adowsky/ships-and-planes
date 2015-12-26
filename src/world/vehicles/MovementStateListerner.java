package world.vehicles;

import world.vehicles.movement.MovingState;

/**
 * Listener of Movement state change.
 */
public interface MovementStateListerner {
    void movementStateChanges(MovingState state);
}
