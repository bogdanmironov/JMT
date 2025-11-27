package bg.sofia.uni.fmi.mjt.itinerary.exception;

public class NodeNotFoundException extends Exception {

    public NodeNotFoundException(String message) {
        super(message);
    }

    public NodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
