package world;

import javafx.geometry.Bounds;

import java.io.Serializable;

/**
 *  Data structure: circle
 */
public class Circle implements Serializable{
    private static long serialVersionUID = 1L;
    private double x;
    private double y;
    private double r;

    /**
     * Constructor
     * @param x X coordinate
     * @param y Y coordinate
     * @param radius Radius
     */
    public Circle(double x, double y, double radius){
        this.x = x;
        this.y = y;
        r = radius;
    }

    /**
     * Returns X coordinate of Circle's center.
     * @return X coordinate of Circle's center.
     */
    public double getCenterX(){
        return x;
    }

    /**
     * Returns Y coordinate of Circle's center.
     * @return
     */
    public double getCenterY(){
        return y;
    }

    /**
     * Returns if object intersects with the circle.
     * @param bounds bounds of object.
     * @return if object intersects with the circle.
     */
    public boolean intersects(Bounds bounds){
       return new javafx.scene.shape.Circle(x,y,r).intersects(bounds);
    }

    /**
     * Returns bounds of circle.
     * @return bounds of circle.
     */
    public javafx.scene.shape.Circle getBounds(){
        return new javafx.scene.shape.Circle(x,y,r);
    }

}
