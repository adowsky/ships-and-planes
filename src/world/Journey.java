package world;

import world.ports.CivilianPort;

import java.io.Serializable;
import java.util.List;

/**
 * Represents passenger's journey.
 */
public class Journey implements Serializable{
    private boolean business;
    private int duration;
    private List<CivilianPort> route;
    private int lastVisitedPortIndex;

    /**
     * Creates new Journey
     * @param isBusiness if journey is business
     * @param duration stay duration
     * @param route list of ports
     */
    public Journey(boolean isBusiness,int duration,List<CivilianPort>route){
        business=isBusiness;
        this.duration=duration;
        this.route=route;
        lastVisitedPortIndex=0;
    }
    public Journey(){

    }
    /**
     * Returns last visited port
     * @return last visited port
     */
    public int getLastVisitedPortIndex() {
        return lastVisitedPortIndex;
    }

    /**
     * Sets last visited port
     * @param lastVisitedPortIndex last visited port
     */
    public void setLastVisitedPortIndex(int lastVisitedPortIndex) { this.lastVisitedPortIndex = lastVisitedPortIndex;
    }

    /**
     * Returns if journey is business
     * @return true or false
     */
    public boolean isBusiness() {
        return business;
    }

    /**
     * sets journey as business
     * @param business true if journey is business or false not
     */
    public void setBusiness(boolean business) {
        this.business = business;
    }

    /**
     * Returns duration of stay.
     * @return duration of stay.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets duration of stay.
     * @param duration duration of stay.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Returns route of journey
     * @return route of journey
     */
    public List<CivilianPort> getRoute() {
        return route;
    }

    /**
     * Sets route of journey
     * @param route route of journey
     */
    public void setRoute(List<CivilianPort> route) {
        this.route = route;
        lastVisitedPortIndex = 0;
    }

    /**
     * Returns amount of ports in route.
     * @return amount of ports in route.
     */

    public int getNumberOfPortsInRoute(){
        return route.size();
    }

    /**
     * Returns Port from collection specified by index.
     * @param index specific index.
     * @return Port from collection specified by index.
     */
    public CivilianPort getPortByIndex(int index){
        return  route.get(index);
    }

}
