package org.n52.gfz.riesgos.exceptions;

/**
 * Exception on non empty stderr
 */
public class NonEmptyStderrException extends Exception {

    /**
     *
     * @param text text of the stderr stream
     */
    public NonEmptyStderrException(final String text) {
        super(text);
    }
}
