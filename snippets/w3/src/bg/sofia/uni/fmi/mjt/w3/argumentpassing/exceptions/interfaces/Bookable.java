package bg.sofia.uni.fmi.mjt.w3.argumentpassing.exceptions.interfaces;

import java.io.IOException;

public interface Bookable {

    boolean book(String request) throws IOException;

}