package bg.sofia.uni.fmi.mjt.foodanaliyzer.exception;

public class CommandFormattingException extends Exception {
    public CommandFormattingException(String message) {
        super(message);
    }

    public CommandFormattingException(String message, Throwable cause) {
        super(message, cause);
    }
}
