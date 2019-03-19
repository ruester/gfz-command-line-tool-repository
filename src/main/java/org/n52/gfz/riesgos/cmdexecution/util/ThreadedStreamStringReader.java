package org.n52.gfz.riesgos.cmdexecution.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class ThreadedStreamStringReader extends Thread {

    private final InputStream inputStream;
    private final StringBuilder resultBuilder;
    private Optional<IOException> optionalException;

    public ThreadedStreamStringReader(final InputStream inputStream) {
        this.inputStream = inputStream;
        this.resultBuilder = new StringBuilder();
        this.optionalException = Optional.empty();
    }

    @Override
    public void run() {

        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = null;
            while((line = reader.readLine()) != null) {
                resultBuilder.append(line);
                resultBuilder.append(System.lineSeparator());
            }
        } catch(final IOException exception) {
            optionalException = Optional.of(exception);
        }
    }

    public void throwExceptionIfNecessary() throws IOException {
        if(optionalException.isPresent()) {
            throw optionalException.get();
        }
    }

    public String getResult() {
        return resultBuilder.toString();
    }
}
