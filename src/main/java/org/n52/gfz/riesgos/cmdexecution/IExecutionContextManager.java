package org.n52.gfz.riesgos.cmdexecution;

import java.util.List;

/**
 * Interface for an execution context manager (for creating an execution context)
 */
public interface IExecutionContextManager {

    /**
     * creates an execution context (maybe inside of docker, depending on the implmentation)
     * @param workingDirectory directory to run the code inside
     * @param cmd string list with the command to execute (for example ["python3", "script.py", "arg1", "arg2"]
     * @return execution context to start the process
     */
    IExecutionContext createExecutionContext(final String workingDirectory, final List<String> cmd);
}
