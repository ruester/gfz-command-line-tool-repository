package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.WriteToStdinException;
import org.n52.wps.io.data.IData;

import java.io.PrintStream;

@FunctionalInterface
public interface IWriteToStdin {

    public void writeToStdin(final PrintStream stdin, final IData iData) throws WriteToStdinException;
}
