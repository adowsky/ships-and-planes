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
    private double currentTranslation;
    private final double TRANSLATION = 6.0;
    public void setModel(Vehicle vehicle){
        rotation = 0;
        currentTranslation = 6;
        setRotate(0);
        this.model = vehicle;
        model.addLocationChangedListener(this);
    }

    public Vehicle getModel() {
        return model;
    }

    @Override
    public void fire(Point2D location, double rotation, boolean translate){
        if(translate){
            if(currentTranslation > (-TRANSLATION)) {
                currentTranslation -= Math.abs(model.getSpeedX()*5);
            }
        }
        else{
            if(currentTranslation < TRANSLATION)
                currentTranslation += Math.abs(model.getSpeedX()*5);
        }
        if(rotation != this.rotation) {
            Platform.runLater(() -> {
                relocate(location.getX(), location.getY());
                setRotate(rotation);
            });
            this.rotation = rotation;
        }
        else
            Platform.runLater(()->{relocate(location.getX(), location.getY());
                if(translate) {
                    setTranslateX(currentTranslation);
                    setTranslateY(currentTranslation);
                }
                else{
                    setTranslateY(currentTranslation);
                    setTranslateX(currentTranslation);
                }
            });

    }
}
