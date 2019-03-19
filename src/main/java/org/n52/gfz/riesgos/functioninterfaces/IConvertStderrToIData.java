package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

@FunctionalInterface
public interface IConvertStderrToIData {

    public IData convertToIData(final String stderr) throws ConvertToIDataException;
}
