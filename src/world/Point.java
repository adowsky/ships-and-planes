package world;

import java.io.Serializable;

/**
 * world.Point - data structure.
 */
public class Point implements Serializable{
    private static long serialVersionUID = 1L;
    private double x;
    private double y;

    public Point(double x, double y){
        this.x =x;
        this.y = y;
    }

    /**
     * Returns X coordinate.
     * @return X coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets X coordinate
     * @param x X coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns Y coordinate.
     * @return Y coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets Y coordinate.
     * @param y Y coordinate.
     */
    public void setY(double y) {
        this.y = y;
    }
}
