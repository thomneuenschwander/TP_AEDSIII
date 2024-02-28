package domain.exceptions;

public class DuplicateIdException extends RuntimeException {
    public DuplicateIdException(Object id){
        super("ID "+id+" is already persisted.");
    }
}
