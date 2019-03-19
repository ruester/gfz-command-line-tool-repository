package org.n52.gfz.riesgos.exceptions;

/**
 * Exception on converting to a idata
 */
public class ConvertToIDataException extends Exception {

    /**
     *
     * @param reason Throwable with the reason of the exception
     */
    public ConvertToIDataException(final Throwable reason) {

        super(reason);
    }
}
