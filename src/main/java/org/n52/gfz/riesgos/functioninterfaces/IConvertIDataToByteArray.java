package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.wps.io.data.IData;

@FunctionalInterface
public interface IConvertIDataToByteArray {

    public byte[] convertToBytes(final IData idata);
}
