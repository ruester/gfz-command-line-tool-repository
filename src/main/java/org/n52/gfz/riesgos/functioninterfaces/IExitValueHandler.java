package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;

/**
 * Interface for the handling of the exit value
 */
@FunctionalInterface
public interface IExitValueHandler {
    /**
     * handles the exit value
     * @param exitValue value to handle
     * @throws NonZeroExitValueException there may be an exception for non zero exit values
     */
    void handleExitValue(final int exitValue) throws NonZeroExitValueException;
}
