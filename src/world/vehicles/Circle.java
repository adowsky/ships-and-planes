package world.vehicles;

import javafx.geometry.Bounds;

import java.io.Serializable;

/**
 *
 */
public class Circle implements Serializable{
    private double x;
    private double y;
    private double r;

    public Circle(double x, double y, double radius){
        this.x = x;
        this.y = y;
        r = radius;
    }
    public double getCenterX(){
        return x;
    }
    public double getCenterY(){
        return y;
    }
    public double getRadius(){
        return r;
    }
    public boolean intersects(Bounds bounds){
       return new javafx.scene.shape.Circle(x,y,r).intersects(bounds);
    }
    public javafx.scene.shape.Circle getBounds(){
        return new javafx.scene.shape.Circle(x,y,r);
    }

}
