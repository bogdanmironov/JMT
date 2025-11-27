package bg.sofia.uni.fmi.mjt.foodanaliyzer.logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class SimpleCharacterExceptionLogger implements ExceptionLogger {
    final Writer logWriter;

    public SimpleCharacterExceptionLogger(Writer logWriter) {
        this.logWriter = logWriter;
    }

    @Override
    public void logException(Throwable throwable) {
        var bufferedWriter = new PrintWriter(logWriter);
        throwable.printStackTrace(bufferedWriter);
        bufferedWriter.println();
        bufferedWriter.flush();

        if (bufferedWriter.checkError()) {
            System.err.println("Problem with printWriter when logging exception.");
            throwable.printStackTrace(System.err);
        }
    }

    @Override
    public void close() throws IOException {
        logWriter.close();
    }
}
