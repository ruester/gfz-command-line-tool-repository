package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoInputDescriptionType;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoOutputDescriptionType;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Optional;

public interface IIdentifierWithBinding {

    public String getIdentifer();

    public Class<? extends IData> getBindingClass();

    public Optional<ICheckDataAndGetErrorMessage> getValidator();

    public Optional<IConvertIDataToCommandLineParameter> getFunctionToTransformToCmd();

    public Optional<String> getPathToWriteToOrReadFromFile();

    public Optional<IConvertIDataToByteArray> getFunctionToGetBytesToWrite();

    public Optional<IConvertIDataToByteArray> getFunctionToWriteToStdin();

    public Optional<IConvertByteArrayToIData> getFunctionToHandleStderr();

    public Optional<IConvertExitValueToIData> getFunctionToHandleExitValue();

    public Optional<IConvertByteArrayToIData> getFunctionToHandleStdout();

    public Optional<IConvertByteArrayToIData> getFunctionToReadFromBytes();

    public Optional<List<String>> getAllowedValues();

    public Optional<String> getDefaultValue();

    public Optional<List<String>> getSupportedCRSForBBox();

    public Optional<String> getSchema();

}
