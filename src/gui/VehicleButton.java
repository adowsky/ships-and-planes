package gui;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import world.vehicles.*;
import world.vehicles.movement.MovingState;

/**
 * Represents vehicle, contains vehicle model inside.
 */
public class VehicleButton extends Button implements LocationChangedListener, MovementStateListerner, Notifiable{
    private Vehicle model;
    private volatile boolean tick = false;
    private double rotation;
    private double currentTranslation;
    private final double TRANSLATION = 6.0;

    /**
     * Sets model of the button
     * @param vehicle model
     */
    public void setModel(Vehicle vehicle){
        rotation = 0;
        currentTranslation = 6;
        setRotate(0);
        this.model = vehicle;
        model.addLocationChangedListener(this);
        model.addMovementStateChangesListener(this);
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
            SynchronizedUpdateNotifier.getInstance().addToList(this);

            while (needToTranslate) {
                double tmpRotation = rot;
                if (translate) {
                    if (model.getSpeedY()>0)
                    tmpRotation += 90;
                        if (currentTranslation > (-TRANSLATION) ) {
                            synchronized (this) {
                                if(tick) {
                                    currentTranslation -= Math.abs(model.getSpeed());
                                    tick = false;
                                }else
                                    trySleep();
                            }
                        } else {
                            tmpRotation = rotation;
                            needToTranslate = false;
                        }

                } else {
                    if (model.getSpeedY()<=0)
                        tmpRotation += 90;

                        if (currentTranslation < TRANSLATION) {
                            synchronized (this) {
                                if (tick) {
                                    currentTranslation += Math.abs(model.getSpeed());
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
            SynchronizedUpdateNotifier.getInstance().removeFromList(this);
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
}
