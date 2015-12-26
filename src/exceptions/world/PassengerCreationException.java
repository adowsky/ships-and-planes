package exceptions.world;

/**
 * Created by ado on 22.12.15.
 */
public class PassengerCreationException extends RuntimeException{
    public PassengerCreationException(Exception e){
        super(e.getMessage());
    }
}
