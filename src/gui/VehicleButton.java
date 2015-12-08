package gui;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import world.vehicles.LocationChangedListener;
import world.vehicles.Vehicle;

/**
 * Represents vehicle, contains vehicle model inside.
 */
public class VehicleButton extends Button implements LocationChangedListener {
    private Vehicle model;
    private double rotation;
    public void setModel(Vehicle vehicle){
        rotation = 0;
        setRotate(0);
        this.model = vehicle;
        model.addLocationChangedListener(this);
    }

    public Vehicle getModel() {
        return model;
    }

    @Override
    public void fire(Point2D location, double rotation){
        if(rotation != this.rotation) {
            Platform.runLater(() -> {
                relocate(location.getX(), location.getY());
                setRotate(rotation);
            });
            this.rotation = rotation;
        }
        else
            Platform.runLater(()->relocate(location.getX(), location.getY()));

    }
}
