package gui;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import world.ports.Port;

/**
 * Port Button containing Port model. Model does backend logic of app.
 */
public class PortButton<T extends Port> extends Button {
    protected T model;
    private Point2D location;
    private String name;
    private int time;

    public PortButton(){
        super();
        this.model = null;
        location = null;
    }

    public T getModel() {
        return model;
    }

    public synchronized Point2D getLocation() {
        if(location == null)
            location = new Point2D(model.getX(),model.getY());
        return location;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setModel(T model){
        this.model = model;
    }


}
