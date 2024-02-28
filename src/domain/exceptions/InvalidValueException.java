package domain.exceptions;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String attributeName, Object value){
        super("Cannot persist: "+attributeName+" -> "+value);
    }
}
