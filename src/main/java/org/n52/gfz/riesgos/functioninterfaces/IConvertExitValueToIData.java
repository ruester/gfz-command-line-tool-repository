package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

public interface IConvertExitValueToIData {

    public IData convertToIData(final int exitValue) throws ConvertToIDataException;
}
