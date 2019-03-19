package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;

@FunctionalInterface
public interface IStderrHandler {

    public void handleSterr(final String stderr) throws NonEmptyStderrException;
}
