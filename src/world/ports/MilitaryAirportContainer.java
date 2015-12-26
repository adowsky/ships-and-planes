package world.ports;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class MilitaryAirportContainer {
    private static MilitaryAirportContainer instance;
    private Set<MilitaryAirport> set;
    private MilitaryAirportContainer(){
        set = new HashSet<>();
    }
    public synchronized static MilitaryAirportContainer getInstance(){
        if(instance ==  null)
            instance = new MilitaryAirportContainer();
        return instance;
    }
    public Set<MilitaryAirport> getMilitaryAirports(){
        return set;
    }
    public void addToSet(MilitaryAirport m){
        set.add(m);
    }
}
