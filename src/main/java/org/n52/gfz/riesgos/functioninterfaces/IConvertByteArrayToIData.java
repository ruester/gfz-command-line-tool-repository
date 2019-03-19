package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

/**
 * Interface to convert a byte array to an IData element
 */
@FunctionalInterface
public interface IConvertByteArrayToIData {

    /**
     * converts the byte array to an IData element
     * @param content byte array to convert
     * @return IData element
     * @throws ConvertToIDataException exception if there is an internal error / exception on conversion
     */
    IData convertToIData(final byte[] content) throws ConvertToIDataException;
}
