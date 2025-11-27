package bg.sofia.uni.fmi.mjt.order.server.exception;

public class SocketConnectionException extends RuntimeException {
    public SocketConnectionException(String message) {
        super(message);
    }
}
