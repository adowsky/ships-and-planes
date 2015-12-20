package world.vehicles.movement;

import world.vehicles.Vehicle;

import java.util.Random;

/**
 * Moving engine that random crossing and move to it.
 */
public class OnlyCrossingMovingEngine {
    private Vehicle vehicle;
    private Random rand;
    public OnlyCrossingMovingEngine(Vehicle vehicle){
        this.vehicle = vehicle;
        rand = new Random();
    }
}
