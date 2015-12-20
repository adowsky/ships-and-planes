package world.vehicles.movement;

import world.Crossing;
import world.vehicles.MapPointEngine;

/**
 * Moving engine to move vehicle through crossing.
 */
public class CrossingMovingEngine extends MapPointEngine {
    /**
     * Creates Crossing moving engine.
     * @param cross cross of engine.
     */
    public CrossingMovingEngine(Crossing cross){
        super(cross);

    }

    @Override
    public synchronized void runInThread() {
        super.runInThread();
        moveOut();


    }



    }

