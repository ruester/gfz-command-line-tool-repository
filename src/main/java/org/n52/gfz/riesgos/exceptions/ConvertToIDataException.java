package org.n52.gfz.riesgos.exceptions;

public class ConvertToIDataException extends Exception {

    private final Throwable reason;

    public ConvertToIDataException(final Throwable reason) {
        this.reason = reason;
    }
}
