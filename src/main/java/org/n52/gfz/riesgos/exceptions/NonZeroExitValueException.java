package org.n52.gfz.riesgos.exceptions;

/**
 * Exception for a non zero exit value
 */
public class NonZeroExitValueException extends Exception {

    /**
     *
     * @param exitValue value of the exit value
     */
    public NonZeroExitValueException(final int exitValue) {
        super("Exit value is " + exitValue);
    }
}
