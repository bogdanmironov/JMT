package bg.sofia.uni.fmi.mjt.udemy.exception;

public class MaxCourseCapacityReachedException extends Exception {
    public MaxCourseCapacityReachedException() {
        super("Cannot add more courses to account.");
    }
}
