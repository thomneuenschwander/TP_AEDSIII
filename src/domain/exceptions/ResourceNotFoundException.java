package domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(int id) {
        super("Resource with ID " + id + " not found.");
    }
}
