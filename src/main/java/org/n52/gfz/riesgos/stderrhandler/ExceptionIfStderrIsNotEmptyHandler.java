package org.n52.gfz.riesgos.stderrhandler;

import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

public class ExceptionIfStderrIsNotEmptyHandler implements IStderrHandler {

    @Override
    public void handleSterr(String stderr) throws NonEmptyStderrException {
        if(! stderr.isEmpty()) {
            throw new NonEmptyStderrException(stderr);
        }

    }
}
