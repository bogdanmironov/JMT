package bg.sofia.uni.fmi.mjt.itinerary.exception;

public class JourneyNotFoundException extends Exception {

    public JourneyNotFoundException(String message) {
        super(message);
    }

    public JourneyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
