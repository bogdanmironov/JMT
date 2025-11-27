package bg.sofia.uni.fmi.mjt.foodanaliyzer.exception;

public class BarcodeImageException extends Exception {
    public BarcodeImageException(String message) {
        super(message);
    }

    public BarcodeImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
