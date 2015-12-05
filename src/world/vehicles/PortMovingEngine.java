 package world.vehicles;

import world.ports.Port;


/**
 * Moves vehicle to the Port.
 */
public class PortMovingEngine extends MapPointEngine{

    public PortMovingEngine(Port port){
        super(port);
    }

}
