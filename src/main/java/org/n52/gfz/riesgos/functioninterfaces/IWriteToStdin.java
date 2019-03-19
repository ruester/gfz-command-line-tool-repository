package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.WriteToStdinException;
import org.n52.wps.io.data.IData;

import java.io.PrintStream;

/**
 * Interface for writing to stdin
 */
@FunctionalInterface
public interface IWriteToStdin {

    /**
     * writes the IData to stdin
     * @param stdin stdin stream of the process
     * @param iData idata to write
     * @throws WriteToStdinException there may be an exception on inner conversion to write the values to stdin
     */
    void writeToStdin(final PrintStream stdin, final IData iData) throws WriteToStdinException;
}
