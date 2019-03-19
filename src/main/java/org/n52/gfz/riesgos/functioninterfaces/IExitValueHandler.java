package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;

@FunctionalInterface
public interface IExitValueHandler {
    public void handleExitValue(final int exitValue) throws NonZeroExitValueException;
}
