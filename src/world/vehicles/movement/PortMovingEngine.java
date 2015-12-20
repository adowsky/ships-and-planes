 package world.vehicles.movement;

import world.ports.Port;
import world.vehicles.MapPointEngine;


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

}
