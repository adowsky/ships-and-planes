package world.vehicles.types.water;

import world.ports.Port;
import world.vehicles.ArmamentType;
import world.vehicles.commons.DestroyListener;
import world.vehicles.Vehicle;
import world.vehicles.movement.Cross;
import world.vehicles.movement.engine.MovingEngineType;
import world.vehicles.movement.RegisteringPortAdapter;
import world.vehicles.types.air.MilitaryAircraft;

import java.io.Serializable;
import java.util.*;

/**
 * Class represents aircraft carrier.  It have ship's properties and it is a vehicle factory.
 */
public class AircraftCarrier extends Ship implements Serializable, DestroyListener {
    private static long serialVersionUID = 1L;
    private ArmamentType armament;
    private Set<MilitaryAircraft> producedPlanes;
    private Port lastVisitedPort;
    private Port nextPort;
    private static Map<Port, Map<Port, List<Cross>>> flightRoutes;
    private static Map<Port, RegisteringPortAdapter> adapters;
    /**
     * Creates airport.
     * @param location
     * @param maxVelocity
     * @param armament
     */
    public AircraftCarrier(Port location, double maxVelocity, ArmamentType armament){
        super(location.getLocation(),maxVelocity, MovingEngineType.DODGING_PORTS);
        this.armament=armament;
        producedPlanes = new HashSet<>();
        lastVisitedPort = location;
        nextPort = location;
        Port newPort = randNewPort();
        setRoute(location.getRouteToPort(newPort));
        nextPort = newPort;
    }
    /**
     * Returns the armament type of the aircraft carrier's instance.
     * @return Armament type of the aircraft carrier's instance
     */
    public ArmamentType getArmament() {
        return armament;
    }
    public static void setFlightRoutes(Map<Port, Map<Port, List<Cross>>> map ){
        flightRoutes = map;
        map.keySet().forEach((e)->
            addAdapter(e, new RegisteringPortAdapter(e.getLocation()))
        );
    }
    public static void addAdapter(Port port, RegisteringPortAdapter adapter){
        if(adapters == null)
            adapters = new HashMap<>();
        adapters.put(port, adapter);
    }

    public static Map<Port, Map<Port, List<Cross>>> getFlightRoutes(){
        return flightRoutes;
    }
    public static Map<Port, RegisteringPortAdapter> getAdapters(){
        return adapters;
    }
    public static void setAdapters(Map<Port, RegisteringPortAdapter> adap){
        adapters = adap;
    }
    /**
     * Sets armament type.
     * @param armament armament type to set.
     */
    public void setArmament(ArmamentType armament) {
        this.armament = armament;
    }


    @Override
    public void draw() {

    }
    @Override
    public void nextCrossing(){
        if(isOnRouteFinish()){
            releaseAircrafts();
            removeFromPreviousCrossingRegister();
            Port newPort = randNewPort();
            lastVisitedPort = nextPort;
            setRoute(nextPort.getRouteToPort(newPort));
            nextPort = newPort;
        }
        else{
            super.nextCrossing();
        }
    }

    private Port randNewPort(){
        Port next = getNextPort();
        if(next != null) {
            Set<Port> rout = getNextPort().getAllRoutes();
            Random random = new Random();
            int target = random.nextInt(rout.size());
            int i = 0;
            for (Port p : rout) {
                if (i == target) {
                    return p;
                }
                i++;
            }
        }else{

        }
        return null;
    }

    @Override
    public Port getNextPort() {
        return nextPort;
    }

    @Override
    public Port getDestination() {
        return getNextPort();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String,String> map = new HashMap<>();
        map.put("Armament type:", getArmament().toString());
        map.put("Location: ",
                Double.toString(getLocation().getX()).split("\\.")[0]+"x"+Double.toString(getLocation().getY()).split("\\.")[0]);
        map.put("ID: ",Integer.toString(getId()));
        return map;
    }

    @Override
    public Port getLastPort() {
        return lastVisitedPort;
    }

    @Override
    public void decreasePortIndex() {

    }

    @Override
    public List<String> getTravelRoute() {
        return Collections.emptyList();
    }

    @Override
    public void editRoute(List<? extends Port> route) {

    }

    private void releaseAircrafts(){
        final Map<Port,List<Cross>> map = flightRoutes.get(getNextPort());
        if(map == null)
            return;
        List<MilitaryAircraft> toRemove = new LinkedList<>();
        producedPlanes.forEach((o) ->{
            List<Cross> route = map.get(o.getNextPort());
            if(route != null){
                o.removeDestroyListener(this);
                o.setAbstractStartRoute(adapters.get(getNextPort()));
                o.setRoute(route);
                o.setReadyToTravel();
                toRemove.add(o);
            }
        });
        producedPlanes.removeAll(toRemove);
    }

    /**
     * Add produced plane to collection
     * @param plane  produced plane
     */
    public void addProducedPlane(MilitaryAircraft plane){
        producedPlanes.add(plane);
        plane.addDestroyListener(this);
    }

    public Set<MilitaryAircraft> getProducedPlanes() {
        return producedPlanes;
    }

    @Override
    public void objectDestroyed(Vehicle o) {
        producedPlanes.remove(o);
    }
}
