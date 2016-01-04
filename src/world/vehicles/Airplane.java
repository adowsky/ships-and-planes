package world.vehicles;

import javafx.geometry.Point2D;
import world.WorldConstants;
import world.vehicles.movement.MovingEngineTypes;

/**
 * Represents plane.
 */
public abstract class Airplane extends Vehicle {
    private int staffAmount;
    private double currentFuel;
    private double maxFuel;


    /**
     * Creates airplane
     * @param location point on map
     * @param staffAmount staff number
     * @param maxFuel maximum value of fuel
     */
    public Airplane(Point2D location,  double speed, int staffAmount, double maxFuel){
        super(location, speed);
        this.staffAmount=staffAmount;
        if(maxFuel >= WorldConstants.MAX_FUEL_VALUE)
            this.maxFuel=maxFuel;
        else
            this.maxFuel = WorldConstants.MAX_FUEL_VALUE;
        currentFuel=maxFuel;
    }
    public Airplane(Point2D location, double speed, int staffAmount, double maxFuel, MovingEngineTypes type){
        super(location, speed, type);
        this.staffAmount=staffAmount;
        if(maxFuel >= WorldConstants.MAX_FUEL_VALUE)
            this.maxFuel=maxFuel;
        else
            this.maxFuel = WorldConstants.MAX_FUEL_VALUE;
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
    public double getCurrentFuel() {
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
    public double getMaxFuel() {
        return maxFuel;
    }

    /**
     * Sets Maximum value of fuel
     * @param maxFuel Maximum value of fuel
     */
    public void setMaxFuel(double maxFuel) {
        this.maxFuel = maxFuel;
    }

    @Override
    public synchronized void move(){
        super.move();
        currentFuel -= getSpeed();
    }

}
