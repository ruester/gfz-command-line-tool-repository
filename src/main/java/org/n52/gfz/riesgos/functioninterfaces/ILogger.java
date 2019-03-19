package org.n52.gfz.riesgos.functioninterfaces;

/**
 * Interface for a logger
 */
@FunctionalInterface
public interface ILogger {

    /**
     * Logs the text
     * @param text text to be logged
     */
    void log(final String text);
}
