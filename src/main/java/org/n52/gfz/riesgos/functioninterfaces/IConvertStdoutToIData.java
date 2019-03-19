package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

/**
 * Interface to convert the stdout text to in IData element
 */
@FunctionalInterface
public interface IConvertStdoutToIData {

    /**
     * converts the stdout text to an IData element
     * @param stdout text to convert
     * @return IData element
     * @throws ConvertToIDataException exception if there is an error / exception on conversion
     */
    IData convertToIData(final String stdout) throws ConvertToIDataException;
}
