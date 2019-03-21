package org.n52.gfz.riesgos.stderrhandler;

import org.n52.gfz.riesgos.functioninterfaces.ILogger;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

/**
 * Handler for stderr that logs the stderr text
 */
public class LogStderrHandler implements IStderrHandler {

    private final ILogger logger;

    public LogStderrHandler(final ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void handleSterr(String stderr) {
        logger.log("Text on stderr:\n" + stderr);
    }
}
