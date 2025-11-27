package bg.sofia.uni.fmi.mjt.w3.argumentpassing.exceptions;

public class EmailNotFoundException extends AuthenticationException {

    public EmailNotFoundException() {
        super("Email not found");
    }

}