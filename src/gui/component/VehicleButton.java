package gui.component;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import world.vehicles.*;
import world.vehicles.commons.LocationChangedListener;
import world.vehicles.commons.MovementStateListener;
import world.vehicles.commons.Notifiable;
import world.vehicles.commons.SynchronizedUpdateNotifier;
import world.vehicles.movement.MovingState;

import java.io.Serializable;

/**
 * Represents vehicle, contains vehicle model inside.
 */
public class VehicleButton extends Button implements LocationChangedListener, MovementStateListener, Notifiable, Serializable{
    private Vehicle model;
    private volatile boolean tick = false;
    private double rotation;
    private double currentTranslation;
    private final double TRANSLATION = 9.0;

    /**
     * Sets model of the button
     * @param vehicle model
     */
    public void setModel(Vehicle vehicle){
        rotation = 0;
        currentTranslation = TRANSLATION;
        setRotate(0);
        this.model = vehicle;
        model.addLocationChangedListener(this);
        model.addMovementStateChangesListener(this);
        setLayoutX(model.getLocation().getX());
        setLayoutY(model.getLocation().getY());
    }

    /**
     * Returns model of the button.
     * @return  model of the button.
     */
    public Vehicle getModel() {
        return model;
    }

    @Override
    public void fire(Point2D location, double rotation, boolean translate){
        if(getLayoutX()> 0 && getLayoutY()>0)
            translate(translate, rotation);
        if(rotation != this.rotation) {
            Platform.runLater(() -> {
                relocate(location.getX(), location.getY());
                setRotate(rotation);
            });
            this.rotation = rotation;
        }
        else
            Platform.runLater(()->{relocate(location.getX(), location.getY());
            });

    }

    /**
     * Translates the button if needed.
     * @param translate translation flag
     * @param rot new rotation.
     */
    private void translate(boolean translate, double rot) {
        boolean needToTranslate = (translate && currentTranslation > (-TRANSLATION)) || (!translate && currentTranslation < TRANSLATION);
        if(needToTranslate) {
            SynchronizedUpdateNotifier.INSTANCE.addToList(this);

            while (needToTranslate && !Thread.interrupted()) {
                double tmpRotation = rot;
                if (translate) {
                        if (currentTranslation > (-TRANSLATION) ) {
                            synchronized (this) {
                                if(tick) {
                                    currentTranslation -= 0.03;
                                    tick = false;
                                }else
                                    trySleep();
                            }
                        } else {
                            tmpRotation = rotation;
                            needToTranslate = false;
                        }

                } else {
                        if (currentTranslation < TRANSLATION) {
                            synchronized (this) {
                                if (tick) {
                                    currentTranslation += 0.03;
                                    tick = false;
                                }
                                else
                                    trySleep();
                            }
                        } else {
                            tmpRotation = rotation;
                            needToTranslate = false;
                        }
                    }

                final double rotationToDo = tmpRotation;
                Platform.runLater(() ->{
                    setRotate(rotationToDo);
                    setTranslateX(currentTranslation);
                    setTranslateY(currentTranslation);
                });
            }
            SynchronizedUpdateNotifier.INSTANCE.removeFromList(this);
        }
    }

    @Override
    public synchronized void tick() {
        tick = true;
    }
    private void trySleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void movementStateChanges(MovingState state) {
        if(state == MovingState.MOVING){
            Platform.runLater(()->setVisible(true));
        }else if ( state == MovingState.STAYING)
            Platform.runLater(()->setVisible(false));
    }

    /**
     * Process destroy of object.
     */
    public void destroy(){
        model.destroy();
        model = null;
    }
}
