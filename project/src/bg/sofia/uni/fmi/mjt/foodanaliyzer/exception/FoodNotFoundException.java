package bg.sofia.uni.fmi.mjt.foodanaliyzer.exception;

public class FoodNotFoundException extends Exception {
    public FoodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FoodNotFoundException(String message) {
        super(message);
    }
}
