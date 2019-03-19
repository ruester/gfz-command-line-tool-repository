package org.n52.gfz.riesgos.exceptions;

public class NonEmptyStderrException extends Exception {

    public NonEmptyStderrException(final String text) {
        super(text);
    }
}
