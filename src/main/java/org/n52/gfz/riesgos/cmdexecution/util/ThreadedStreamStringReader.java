package org.n52.gfz.riesgos.cmdexecution.util;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reader that works in another stream to read
 * from a stream.
 * Text is joined to one single string
 */
public class ThreadedStreamStringReader extends Thread {

    /**
     * Input stream to read from.
     */
    private final InputStream inputStream;
    /**
     * String builder to concat the text from the stream.
     */
    private final StringBuilder resultBuilder;
    /**
     * Storage for any exception that may happen on reading.
     */
    private IOException optionalException;

    /**
     * Default constructor.
     * @param aInputStream input stream to read from
     */
    public ThreadedStreamStringReader(final InputStream aInputStream) {
        this.inputStream = aInputStream;
        this.resultBuilder = new StringBuilder();
        this.optionalException = null;
    }

    /**
     * Runs the reading from the input stream.
     * (Should run in a seperate thread).
     */
    @Override
    public void run() {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                resultBuilder.append(line);
                resultBuilder.append(System.lineSeparator());
            }
        } catch (final IOException exception) {
            optionalException = exception;
        }
    }

    /**
     * Throws an io exception if there was an exception on reading.
     * @throws IOException io exception that may happen
     * on reading from the stream
     */
    public void throwExceptionIfNecessary() throws IOException {
        if (optionalException != null) {
            throw optionalException;
        }
    }

    /**
     *
     * @return joined result string of all the streams output
     */
    public String getResult() {
        return resultBuilder.toString();
    }
}
