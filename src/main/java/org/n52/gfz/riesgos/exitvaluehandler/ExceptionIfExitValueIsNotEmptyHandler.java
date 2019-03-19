package org.n52.gfz.riesgos.exitvaluehandler;

import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;

public class ExceptionIfExitValueIsNotEmptyHandler implements IExitValueHandler {

    @Override
    public void handleExitValue(int exitValue) throws NonZeroExitValueException {
        if(exitValue != 0) {
            throw new NonZeroExitValueException(exitValue);
        }
    }
}
