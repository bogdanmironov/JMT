package bg.sofia.uni.fmi.mjt.w3.argumentpassing.exceptions;

public class WrongPasswordException extends AuthenticationException {

    public WrongPasswordException() {
        super("Wrong password");
    }

}