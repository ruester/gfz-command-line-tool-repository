package org.n52.gfz.riesgos.exitvaluehandler;

import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;

public class LogExitValueHandler implements IExitValueHandler {

    private final ILogger logger;

    public LogExitValueHandler(final ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void handleExitValue(int exitValue) throws NonZeroExitValueException {
        logger.log("Exit value: " + exitValue);
    }
}
