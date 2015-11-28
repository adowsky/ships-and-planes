package gui;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import world.vehicles.LocationChangedListener;
import world.vehicles.Ship;
import world.vehicles.Vehicle;

/**
 * Represents vehicle, contains vehicle model inside.
 */
public class VehicleButton extends Button implements LocationChangedListener {
    private Vehicle model;
    public void setModel(Vehicle vehicle){
        this.model = vehicle;
        model.addLocationChangedListener(this);
    }

    public Vehicle getModel() {
        return model;
    }

    @Override
    public void fire(Point2D location){
        System.out.println(location.getX()+"x"+location.getY());
        Platform.runLater(()-> relocate(location.getX(),location.getY()));

    }
}
