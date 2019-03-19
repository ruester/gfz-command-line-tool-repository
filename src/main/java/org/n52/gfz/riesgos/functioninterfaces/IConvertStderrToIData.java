package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

/**
 * Interface for the conversion of stderr text to an IData
 */
@FunctionalInterface
public interface IConvertStderrToIData {

    /**
     * converts the stderr text to an idata
     * @param stderr text to convert
     * @return IData element
     * @throws ConvertToIDataException exception that is thrown on a error / exception on the conversion
     */
    IData convertToIData(final String stderr) throws ConvertToIDataException;
}
