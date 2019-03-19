package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.wps.io.data.IData;

import java.util.List;

@FunctionalInterface
public interface IConvertIDataToCommandLineParameter {

    public List<String> convertToCommandLineParameter(final IData iData);

}
