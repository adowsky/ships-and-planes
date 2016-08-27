 package world.vehicles.movement.engine;

 import world.ports.Port;
 import world.vehicles.movement.engine.MapPointEngine;


 /**
 * Moves vehicle to the Port.
 */
public class PortMovingEngine extends MapPointEngine {
    /**
     * Creates instance of class.
     * @param port
     */
    public PortMovingEngine(Port port){
        super(port);
    }
     @Override
     public synchronized void runInThread(){
         if(getActiveVehicle().isOnRouteFinish())
             super.runInThread();
         else
            moveOut();
     }

}
