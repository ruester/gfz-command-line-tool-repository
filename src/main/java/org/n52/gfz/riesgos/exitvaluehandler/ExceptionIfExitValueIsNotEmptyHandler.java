package org.n52.gfz.riesgos.exitvaluehandler;

import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;

/**
 * Handler for the exit value that throws an exception on a non zero
 * exit value
 */
public class ExceptionIfExitValueIsNotEmptyHandler implements IExitValueHandler {

    @Override
    public void handleExitValue(int exitValue) throws NonZeroExitValueException {
        if(exitValue != 0) {
            throw new NonZeroExitValueException(exitValue);
        }
    }
}
