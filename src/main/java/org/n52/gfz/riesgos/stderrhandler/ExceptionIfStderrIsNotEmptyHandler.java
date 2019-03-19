package org.n52.gfz.riesgos.stderrhandler;

import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

/**
 * Handler for stderr that throws an exception on non empty stderr
 */
public class ExceptionIfStderrIsNotEmptyHandler implements IStderrHandler {

    @Override
    public void handleSterr(String stderr) throws NonEmptyStderrException {
        if(! stderr.isEmpty()) {
            throw new NonEmptyStderrException(stderr);
        }

    }
}
