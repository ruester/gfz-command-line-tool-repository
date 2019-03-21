package org.n52.gfz.riesgos.cmdexecution;

import java.io.IOException;

/**
 * Interface for an context to run an executable
 * Examples: a docker container or no special context (just a normal process)
 */
public interface IExecutionContext extends AutoCloseable {

    /**
     * Auto closable to maybe remove a created container
     */
    void close();

    /**
     * runs the executable
     * @return interface for the run (to provide access to stdin)
     * @throws IOException there may be an IOException on starting the run
     */
    IExecutionRun run() throws IOException;

    /**
     * Reads a file from the path (maybe out of a docker container)
     * @param path path of a file
     * @return byte array content
     * @throws IOException there may be an IOException on reading the file
     */
    byte[] readFromFile(final String path) throws IOException;

    /**
     * Write the contents of a byte array to a path (maybe in a docker container)
     * @param content byte array with the data
     * @param workingDir working directory to write to
     * @param fileName filename in the working directory
     * @throws IOException there may be an IOException on wriring the file
     */
    void writeToFile(final byte[] content, final String workingDir, final String fileName) throws IOException;
}
