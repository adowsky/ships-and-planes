package world;

import exceptions.world.PassengerCreationException;
import world.ports.CivilianPort;
import world.ports.Port;

import java.util.*;

/**
 * Generates Passengers.
 */
public class PassengerGenerator {
    private static PassengerGenerator instance;
    private Random rand;
    private final List<FirstName> FIRST_NAMES;
    private final List<LastName> LAST_NAMES;
    private List<CivilianPort> portList;
    private PassengerGenerator(){
        rand = new Random();
        FIRST_NAMES = Collections.unmodifiableList(Arrays.asList(FirstName.values()));
        LAST_NAMES = Collections.unmodifiableList(Arrays.asList(LastName.values()));
    }

    public synchronized static PassengerGenerator getInstance(){
        if (instance == null)
            instance = new PassengerGenerator();
        return instance;
    }

    /**
     * Returns set of newly created passengers.
     * @param amount number of passengers to create
     * @return set of newly created passengers.
     */
    public Set<Passenger> getPassengers(int amount){
        Set<Passenger> set = new HashSet<>();
        for(int i=0; i<amount; i++){
            set.add(getPassenger());
        }
        return set;
    }

    /**
     * Returns new random generated Passenger.
     * @return random generated Passenger.
     */
    public Passenger getPassenger(){
        Passenger passenger = new Passenger();
        passenger.setFirstname(getRandomFirstName().toString());
        passenger.setLastname(getRandomLastName().toString());
        passenger.setAge(rand.nextInt(60)+1);
        try {
            passenger.newJourney(portList);
        }catch (Exception ex){
            throw new PassengerCreationException(ex);
        }
        passenger.setPesel(randPesel());
        return passenger;
    }
    private FirstName getRandomFirstName(){
        int index = rand.nextInt(FIRST_NAMES.size());
        return FIRST_NAMES.get(index);

    }
    private LastName getRandomLastName(){
        int index = rand.nextInt(LAST_NAMES.size());
        return LAST_NAMES.get(index);
    }
    private String randPesel(){
        StringBuilder pesel  = new StringBuilder();
        for(int i=0; i<11; i++){
            pesel.append(rand.nextInt(10));
        }
        return pesel.toString();
    }
    public void setPortDataBase(List<CivilianPort> list){
        portList = list;

    }
    private enum FirstName {
        Jack, Timothy, George, Peter, Ahmed, John, Thomas, Richard,
        Ann, Penelope, Jessica, Sara, Jane, Marry, Kate, Natalie
    }
    private enum LastName{
        Dickson, Seward, Jackson, Johnson, Smith, Doe, Black, White, Bond, Dickinson, Portman, Potter, Granger,
        Williams
    }
}
