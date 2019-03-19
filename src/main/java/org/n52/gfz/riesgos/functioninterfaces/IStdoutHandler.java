package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.wps.io.data.IData;

/**
 * Handler for stdout text
 */
@FunctionalInterface
public interface IStdoutHandler {

    /**
     * Handles the text on stdout
     * @param stdout
     */
    void handleStdout(final String stdout);
}
