package org.n52.gfz.riesgos.cmdexecution.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reader that works in another stream to read
 * from a stream.
 * Text is concated to one single string
 */
public class ThreadedStreamStringReader extends Thread {

    private final InputStream inputStream;
    private final StringBuilder resultBuilder;
    private IOException optionalException;

    /**
     *
     * @param inputStream input stream to read from
     */
    public ThreadedStreamStringReader(final InputStream inputStream) {
        this.inputStream = inputStream;
        this.resultBuilder = new StringBuilder();
        this.optionalException = null;
    }

    @Override
    public void run() {

        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while((line = reader.readLine()) != null) {
                resultBuilder.append(line);
                resultBuilder.append(System.lineSeparator());
            }
        } catch(final IOException exception) {
            optionalException = exception;
        }
    }

    /**
     * throws an io exception if there was an exception on reading
     * @throws IOException io exception that may happen on reading from the stream
     */
    public void throwExceptionIfNecessary() throws IOException {
        if(optionalException != null) {
            throw optionalException;
        }
    }

    /**
     *
     * @return concated result string of all the streams output
     */
    public String getResult() {
        return resultBuilder.toString();
    }
}
