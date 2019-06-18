package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;

import java.util.Optional;

/**
 * Configuration of an output parameter.
 */
public interface IOutputParameter extends IIOParameter {

    /**
     *
     * @return function to convert a byte array to the value; used to
     * convert from stderr
     */
    Optional<IConvertByteArrayToIData> getFunctionToHandleStderr();

    /**
     *
     * @return function to convert a integer exit value to a idata
     * value; used for the handling of the exit value of the overall
     * program
     */
    Optional<IConvertExitValueToIData> getFunctionToHandleExitValue();

    /**
     *
     * @return function to convert a byte array to the value;
     * used to convert from stdout
     */
    Optional<IConvertByteArrayToIData> getFunctionToHandleStdout();

    /**
     *
     * @return function to read the iData from files
     */
    Optional<IReadIDataFromFiles> getFunctionToReadIDataFromFiles();
}
