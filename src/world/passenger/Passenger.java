package world.passenger;

import world.ports.CivilianPort;
import world.vehicles.Vehicle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents passenger.
 */
public class Passenger implements Serializable {
    private static long serialVersionUID = 1L;
    private String firstname;
    private String lastname;
    private String pesel;
    private int age;
    private Vehicle currentVehicle;
    private Journey journey;
    private List<CivilianPort> db;
    private boolean waiting;

    /**
     * Creates new Passenger
     *
     * @param firstName first name
     * @param lastname  last name
     * @param pesel     PESEL
     * @param age       age of guy
     * @param journey   journey details
     */
    public Passenger(String firstName, String lastname, String pesel, int age, Journey journey) {
        this.firstname = firstName;
        this.lastname = lastname;
        this.pesel = pesel;
        this.age = age;
        this.journey = journey;
        currentVehicle = null;
    }

    public Passenger() {

    }

    /**
     * Returns Last name of passenger.
     *
     * @return Last name of passenger.
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets last name of passenger.
     *
     * @param lastname last name of passenger.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Returns PESEL of passenger.
     *
     * @return PESEL of passenger.
     */
    public String getPesel() {
        return pesel;
    }

    /**
     * Sets PESEL of passenger.
     *
     * @param pesel PESEL of passenger.
     */
    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    /**
     * Returns age of passenger.
     *
     * @return age of passenger.
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets age of passenger.
     *
     * @param age age of passenger.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns vehicle that contains the passenger.
     *
     * @return vehicle that contains the passenger.
     */
    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    /**
     * Sets  vehicle that contains the passenger.
     *
     * @param currentVehicle vehicle that contains the passenger.
     */
    public void setCurrentVehicle(Vehicle currentVehicle) {
        this.currentVehicle = currentVehicle;
    }

    /**
     * Returns journey details.
     *
     * @return journey details.
     */
    public Journey getJourney() {
        return journey;
    }

    /**
     * Sets journey details.
     *
     * @param journey journey details.
     */
    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    /**
     * Returns first name of passenger.
     *
     * @return first name of passenger.
     */
    public String getFirstname() {

        return firstname;
    }

    /**
     * Sets first name of passenger.
     *
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
     * Notify passenger about visiting next port.
     */
    public void nextPortIsVisited() {
        List<CivilianPort> route = journey.getRoute();
        int lastVisitedPortIndex = journey.getLastVisitedPortIndex();
        if ((route.size() - 1) < lastVisitedPortIndex)
            journey.setLastVisitedPortIndex(lastVisitedPortIndex + 1);
        else {
            newJourney(db);
            waiting = true;
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(journey.getDuration());
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    setWaiting(false);
                }
            });
            t.setDaemon(true);
            t.start();
        }

    }

    /**
     * Sets waiting flag
     *
     * @param t flag
     */
    public synchronized void setWaiting(boolean t) {
        waiting = t;
    }

    /**
     * Returns waiting flag in port.
     *
     * @return waiting flag.
     */
    public synchronized boolean isWaiting() {
        return waiting;
    }

    /**
     * Returns last visited port by passenger.
     *
     * @return last visited port by passenger.
     */
    public CivilianPort getLastVisitedPort() {
        List<CivilianPort> route = journey.getRoute();
        int lastVisited = journey.getLastVisitedPortIndex();
        return (lastVisited >= journey.getNumberOfPortsInRoute() - 1) ? route.get(lastVisited) : route.get(lastVisited + 1);
    }

    /**
     * Returns port that passenger travelling to.
     *
     * @return port that passenger travelling to.
     */
    public CivilianPort getNextPortToVisit() {
        if (journey.getLastVisitedPortIndex() + 1 < journey.getNumberOfPortsInRoute()) {
            return journey.getPortByIndex(journey.getLastVisitedPortIndex() + 1);
        } else {
            return journey.getPortByIndex(journey.getLastVisitedPortIndex());
        }
    }

    /**
     * Immediately travels to the next port.
     */
    public void moveToTheNextPortNow() {
        getLastVisitedPort().passengerWentAway(this);
        getNextPortToVisit().passengerHasCome(this);
        nextPortIsVisited();
    }

    /**
     * Makes new Journey for passenger.
     *
     * @param db ports database.
     */
    public void newJourney(List<CivilianPort> db) {
        if (this.db == null)
            this.db = db;
        // non determined time because of condition using random function.
        List<CivilianPort> route = new ArrayList<>();
        Random rand = new Random();
        if (journey == null) {
            journey = new Journey();
            route.add(db.get(rand.nextInt(db.size())));
            route.get(0).passengerHasCome(this);
        } else {
            route.add(journey.getRoute().get(journey.getRoute().size() - 1));
        }
        int size = rand.nextInt(4) + 1;
        for (int i = 0; i < size; i++) {
            boolean found = false;
            while (!found) {
                List<CivilianPort> set = route.get(i).getAllConnections();
                CivilianPort randed = set.get(rand.nextInt(set.size()));
                if (!route.contains(randed)) {
                    route.add(randed);
                    found = true;
                }
            }
        }
        journey.setRoute(route);
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (waiting) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(journey.getDuration());
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    setWaiting(false);
                }
            });
            t.setDaemon(true);
            t.start();

        }
    }
}
