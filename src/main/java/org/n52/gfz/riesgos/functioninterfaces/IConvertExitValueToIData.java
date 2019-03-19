package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

/**
 * Interface for converting the exit value to an IData element
 */
public interface IConvertExitValueToIData {

    /**
     * converts the exit value to an IData element
     * @param exitValue integer value to convert
     * @return IData element
     * @throws ConvertToIDataException exception if there is an internal error / exception on conversion
     */
    IData convertToIData(final int exitValue) throws ConvertToIDataException;
}
