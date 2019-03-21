package org.n52.gfz.riesgos.cmdexecution;

/**
 * Interface to represent a result of a process.
 */
public interface IExecutionRunResult  {

    /**
     *
     * @return exit value of the process
     */
    int getExitValue();

    /**
     *
     * @return concated string of stderr stream
     */
    String getStderrResult();

    /**
     *
     * @return concated string of stdout stream
     */
    String getStdoutResult();
}
