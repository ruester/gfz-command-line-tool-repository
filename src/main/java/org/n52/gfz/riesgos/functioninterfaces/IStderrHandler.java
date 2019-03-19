package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;

/**
 * Handler for text from stderr
 */
@FunctionalInterface
public interface IStderrHandler {

    /**
     * handles stderr text
     * @param stderr text to handle
     * @throws NonEmptyStderrException there may be an exception on non empty stderr
     */
    void handleSterr(final String stderr) throws NonEmptyStderrException;
}
