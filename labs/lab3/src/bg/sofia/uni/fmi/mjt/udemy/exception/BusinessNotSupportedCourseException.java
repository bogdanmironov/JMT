package bg.sofia.uni.fmi.mjt.udemy.exception;

public class BusinessNotSupportedCourseException extends IllegalArgumentException {
    public BusinessNotSupportedCourseException() {
        super("Business account does not allow for this category");
    }
}
