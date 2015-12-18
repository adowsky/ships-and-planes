package world;

import world.ports.CivilianPort;
import world.vehicles.Vehicle;

import java.util.List;

/**
 * Represents passenger.
 */
public class Passenger {
    private String firstname;
    private String lastname;
    private String pesel;
    private int age;
    private Vehicle currentVehicle;
    private Journey journey;

    /**
     * Creates new Passenger
     * @param firstName first name
     * @param lastname last name
     * @param pesel PESEL
     * @param age age of guy
     * @param journey journey details
     */
    public Passenger(String firstName,String lastname,String pesel, int age, Journey journey){
        this.firstname=firstName;
        this.lastname=lastname;
        this.pesel=pesel;
        this.age=age;
        this.journey=journey;
        currentVehicle=null;
    }

    /**
     * Returns Last name of passenger.
     * @return Last name of passenger.
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets last name of passenger.
     * @param lastname last name of passenger.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Returns PESEL of passenger.
     * @return PESEL of passenger.
     */
    public String getPesel() {
        return pesel;
    }

    /**
     * Sets PESEL of passenger.
     * @param pesel PESEL of passenger.
     */
    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    /**
     * Returns age of passenger.
     * @return  age of passenger.
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets age of passenger.
     * @param age  age of passenger.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns vehicle that contains the passenger.
     * @return  vehicle that contains the passenger.
     */
    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    /**
     * Sets  vehicle that contains the passenger.
     * @param currentVehicle vehicle that contains the passenger.
     */
    public void setCurrentVehicle(Vehicle currentVehicle) {
        this.currentVehicle = currentVehicle;
    }

    /**
     * Returns journey details.
     * @return journey details.
     */
    public Journey getJourney() {
        return journey;
    }

    /**
     * Sets journey details.
     * @param journey journey details.
     */
    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    /**
     * Returns first name of passenger.
     * @return first name of passenger.
     */
    public String getFirstname() {

        return firstname;
    }

    /**
     * Sets first name of passenger.
     * @param firstname first name of passenger.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Random new Route for passenger.
     */
    public void randomNewRoute() {
    }

    /**
     * Changes last visited port to next.
     */
    public void visitNextCity(){
        int nextPort = journey.getLastVisitedPortIndex()+1;
        journey.setLastVisitedPortIndex(nextPort);
    }

    /**
     * Notify passenger about visiting next port.
     */
    public void nextPortIsVisited(){
        List<CivilianPort> route = journey.getRoute();
        int lastVisitedPortIndex = journey.getLastVisitedPortIndex();
        if((route.size() - 1) < lastVisitedPortIndex)
            journey.setLastVisitedPortIndex(lastVisitedPortIndex + 1);
    }

    /**
     * Returns last visited port by passenger.
     * @return last visited port by passenger.
     */
    public CivilianPort getLastVisitedPort(){
        //TODO wprowadzić metody bezpieczeństwa, może nie być kolejnej trasy, np jeśli odwiedzona stacja jest ostatnią to nowa trasa.
        List<CivilianPort> route = journey.getRoute();
        int lastVisited = journey.getLastVisitedPortIndex();
        return route.get(lastVisited + 1);
    }

    /**
     * Returns port that passenger travelling to.
     * @return port that passenger travelling to.
     */
    public CivilianPort getNextPortToVisit(){
        if(journey.getLastVisitedPortIndex()+1 < journey.getNumberOfPortsInRoute()){
            return journey.getPortByIndex(journey.getLastVisitedPortIndex()+1);
        }
        else{
            return journey.getPortByIndex(journey.getLastVisitedPortIndex());
        }
    }

    /**
     * Immediately travels to the next port.
     */
    public void moveToTheNextPortNow(){
        getLastVisitedPort().passengerWentAway(this);
        getNextPortToVisit().passengerHasCome(this);
        nextPortIsVisited();
    }

}
