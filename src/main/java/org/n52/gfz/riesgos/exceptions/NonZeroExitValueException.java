package org.n52.gfz.riesgos.exceptions;

public class NonZeroExitValueException extends Exception {

    public NonZeroExitValueException(final int exitValue) {
        super("Exit value is " + exitValue);
    }
}
