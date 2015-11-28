package world.vehicles;

import javafx.geometry.Point2D;

/**
 * Represents plane.
 */
public abstract class Airplane extends Vehicle {
    private int staffAmount;
    private int currentFuel;
    private int maxFuel;


    /**
     * Creates airplane
     * @param location point on map
     * @param staffAmount staff number
     * @param maxFuel maximum value of fuel
     */
    public Airplane(Point2D location,  double speed, int staffAmount, int maxFuel){
        super(location, speed);
        this.staffAmount=staffAmount;
        this.maxFuel=maxFuel;
        currentFuel=maxFuel;
    }
    /**
     * Returns staff amount.
     * @return staff amount.
     */
    public int getStaffAmount() {
        return staffAmount;
    }

    /**
     * Sets staff amount.
     * @param staffAmount staff amount to set.
     */
    public void setStaffAmount(int staffAmount) {
        this.staffAmount = staffAmount;
    }

    /**
     * Returns current fuel.
     * @return current fuel
     */
    public int getCurrentFuel() {
        return currentFuel;
    }

    /**
     * Sets fuel at maximum value.
     */
    public void refuel() {
        currentFuel = getMaxFuel();
    }

    /**
     * Returns maximum value of fuel.
     * @return Maximum value of fuel.
     */
    public int getMaxFuel() {
        return maxFuel;
    }

    /**
     * Sets Maximum value of fuel
     * @param maxFuel Maximum value of fuel
     */
    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }

}
