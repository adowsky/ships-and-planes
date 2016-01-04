package exceptions.gui;

/**
 * Exception that is threw by MapInitializer class during parsing XML map file.
 */
public class MapInitException extends Exception{
    public enum ErrorType{
        PORTS_NOT_INIT,
        FILE_NOT_COHERENT,
        FILE_PARSE;
    }
    private ErrorType type;
    public MapInitException(Exception e, ErrorType err){
        super(e.getMessage());
        type=err;
    }
    public MapInitException(String msg, ErrorType err){
        super(msg);
        type = err;
    }
    public ErrorType getType(){
        return type;
    }
}
