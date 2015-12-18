package world.vehicles;

import javafx.geometry.Point2D;

/**
 * Listener.
 */
public interface LocationChangedListener {
    /**
     * Runs service of locations change.
     * @param location new Location
     * @param rotation new Rotation
     * @param translate if translation is needed.
     */
    void fire(Point2D location, double rotation,boolean translate);
}
