package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.wps.io.data.IData;

import java.util.List;

/**
 * Interface to convert in IData to a string for using it as a command line argument
 */
@FunctionalInterface
public interface IConvertIDataToCommandLineParameter {

    /**
     * converts the IData to a string
     * @param iData element to convert
     * @return String
     */
    List<String> convertToCommandLineParameter(final IData iData);
}
