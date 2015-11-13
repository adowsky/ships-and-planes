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
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public void setCurrentVehicle(Vehicle currentVehicle) {
        this.currentVehicle = currentVehicle;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public String getFirstname() {

        return firstname;
    }

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
    public void nextPortIsVisited(){
        List<CivilianPort> route = journey.getRoute();
        int lastVisitedPortIndex = journey.getLastVisitedPortIndex();
        if((route.size() - 1) < lastVisitedPortIndex)
            journey.setLastVisitedPortIndex(lastVisitedPortIndex + 1);
    }
    public CivilianPort getLastVisitedPort(){
        //TODO wprowadzić metody bezpieczeństwa, może nie być kolejnej trasy, np jeśli odwiedzona stacja jest ostatnią to nowa trasa.
        List<CivilianPort> route = journey.getRoute();
        int lastVisited = journey.getLastVisitedPortIndex();
        return route.get(lastVisited + 1);
    }
    public CivilianPort getNextPortToVisit(){
        if(journey.getLastVisitedPortIndex()+1 < journey.getNumberOfPortsInRoute()){
            return journey.getPortByIndex(journey.getLastVisitedPortIndex()+1);
        }
        else{
            return journey.getPortByIndex(journey.getLastVisitedPortIndex());
        }
    }
    public void moveToTheNextPortNow(){
        getLastVisitedPort().passengerWentAway(this);
        getNextPortToVisit().passengerHasCome(this);
        nextPortIsVisited();
    }

}
