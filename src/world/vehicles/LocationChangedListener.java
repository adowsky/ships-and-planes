package world.vehicles;

import javafx.geometry.Point2D;

/**
 * Listener.
 */
public interface LocationChangedListener {
    void fire(Point2D location);
}
