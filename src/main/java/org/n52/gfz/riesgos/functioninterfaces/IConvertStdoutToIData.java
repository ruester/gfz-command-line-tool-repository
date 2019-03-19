package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

@FunctionalInterface
public interface IConvertStdoutToIData {

    public IData convertToIData(final String stdout) throws ConvertToIDataException;
}
