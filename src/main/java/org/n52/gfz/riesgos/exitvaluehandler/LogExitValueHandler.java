package org.n52.gfz.riesgos.exitvaluehandler;

import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;

/**
 * Handler for the exit value that logs the value
 */
public class LogExitValueHandler implements IExitValueHandler {

    private final ILogger logger;

    /**
     *
     * @param logger object used to log
     */
    public LogExitValueHandler(final ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void handleExitValue(int exitValue) {
        logger.log("Exit value: " + exitValue);
    }
}
