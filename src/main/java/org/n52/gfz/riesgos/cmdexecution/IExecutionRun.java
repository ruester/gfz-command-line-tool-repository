package org.n52.gfz.riesgos.cmdexecution;

import java.io.PrintStream;

/**
 * Interface for a running process to provide
 */
public interface IExecutionRun {

    /**
     * returns the stdin stream of the running process
     * @return stdin stream
     */
    PrintStream getStdin();

    /**
     * Blocks until the process completed.
     * Gives a result back to access stderr and stdout text
     *
     * @return result of the process with access to the exit value and stderr and stdout text
     * @throws InterruptedException there maybe is an interrupted exception on waiting for the process to complete
     */
    IExecutionRunResult waitForCompletion() throws InterruptedException;

}
