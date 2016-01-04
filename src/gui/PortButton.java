package gui;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import world.ports.Port;

import java.io.Serializable;

/**
 * Port Button containing Port model. Model does backend logic of app.
 */
public class PortButton<T extends Port> extends Button implements Serializable{
    protected T model;
    private Point2D location;
    private String name;

    /**
     * Creates new Port button.
     */
    public PortButton(){
        super();
        this.model = null;
        location = null;
    }

    /**
     * Returns model of the button.
     * @return   model of the button.
     */
    public T getModel() {
        return model;
    }

    /**
     * Returns location of the model.
     * @return location of the model.
     */
    public synchronized Point2D getLocation() {
        if(location == null)
            location = new Point2D(model.getX(),model.getY());
        return location;
    }

    /**
     * Sets name of the port.
     * @param name name of the port
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Returns name of the port.
     * @return name of the port.
     */
    public String getName(){
        return name;
    }

    /**
     * Sets button model.
     * @param model button model.
     */
    public void setModel(T model){
        this.model = model;
    }


}
