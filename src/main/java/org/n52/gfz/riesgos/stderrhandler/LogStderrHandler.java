package org.n52.gfz.riesgos.stderrhandler;

import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

public class LogStderrHandler implements IStderrHandler {

    private final ILogger logger;

    public LogStderrHandler(final ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void handleSterr(String stderr) throws NonEmptyStderrException {
        logger.log("Text on stderr:\n" + stderr);
    }
}
