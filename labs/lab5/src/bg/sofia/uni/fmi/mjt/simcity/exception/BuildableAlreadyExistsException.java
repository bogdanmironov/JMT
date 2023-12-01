package bg.sofia.uni.fmi.mjt.simcity.exception;

public class BuildableAlreadyExistsException extends RuntimeException {
    public BuildableAlreadyExistsException() {
        super("Buildable already exists.");
    }
}
