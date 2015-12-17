package gui;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import world.vehicles.LocationChangedListener;
import world.vehicles.Notifiable;
import world.vehicles.Vehicle;

/**
 * Represents vehicle, contains vehicle model inside.
 */
public class VehicleButton extends Button implements LocationChangedListener, Notifiable{
    private Vehicle model;
    private boolean tick = false;
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

        if(rotation != this.rotation) {
            Platform.runLater(() -> {
                relocate(location.getX(), location.getY());
                setRotate(rotation);
                translate(translate);
            });
            this.rotation = rotation;
        }
        else
            Platform.runLater(()->{relocate(location.getX(), location.getY());
            translate(translate);
            });

    }
    private void translate(boolean translate) {
        boolean condition = true;
        while (condition) {
            if (translate) {
                setRotate(rotation-90);
                if (currentTranslation > (-TRANSLATION) && tick) {
                    currentTranslation -= Math.abs(model.getSpeedX() * 7);
                    tick = false;
                } else {
                    condition = false;
                    setRotate(rotation);
                }
            } else {
                setRotate(rotation+90);
                if (currentTranslation < TRANSLATION && tick){
                    currentTranslation += Math.abs(model.getSpeedX() * 7);
                    tick=false;
                }
                else {
                    setRotate(rotation);
                    condition = false;
                }
            }
            if (translate) {
                setTranslateX(currentTranslation);
                setTranslateY(currentTranslation);
            } else {
                setTranslateY(currentTranslation);
                setTranslateX(currentTranslation);
            }
        }
    }

    @Override
    public void tick() {
        tick = true;
    }
}
