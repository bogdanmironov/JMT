package bg.sofia.uni.fmi.mjt.foodanaliyzer.logger;

import java.io.Closeable;

public interface ExceptionLogger extends Closeable {
    void logException(Throwable throwable);
}
