package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.wps.io.data.IData;

/**
 * Interface to convert an IData element to a byte array
 */
@FunctionalInterface
public interface IConvertIDataToByteArray {

    /**
     * converts the IData to a byte array
     * @param idata element to convert
     * @return byte array
     */
    byte[] convertToBytes(final IData idata);
}
