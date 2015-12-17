package exceptions.gui;

import java.util.Map;

/**
 * Exception that is threw by MapInitializer class during parsing XML map file.
 */
public class MapInitException extends Exception{
    public enum ErrorType{

        FILE_PARSE;
    }
    private ErrorType type;
    public MapInitException(Exception e, ErrorType err){
        super(e.getMessage());
        type=err;
    }
    public ErrorType getType(){
        return type;
    }
}
