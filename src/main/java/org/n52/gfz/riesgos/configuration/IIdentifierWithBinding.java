package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertStderrToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertStdoutToIData;
import org.n52.gfz.riesgos.functioninterfaces.IWriteToStdin;
import org.n52.wps.io.data.IData;

import java.util.Optional;

public interface IIdentifierWithBinding {

    public String getIdentifer();

    public Class<? extends IData> getBindingClass();

    public Optional<ICheckDataAndGetErrorMessage> getValidator();

    public Optional<IConvertIDataToCommandLineParameter> getFunctionToTransformToCmd();

    public Optional<String> getPathToWriteToOrReadFromFile();

    public Optional<IConvertIDataToByteArray> getFunctionToGetBytesToWrite();

    public Optional<IWriteToStdin> getFunctionToWriteToStdin();

    public Optional<IConvertStderrToIData> getFunctionToHandleStderr();

    public Optional<IConvertExitValueToIData> getFunctionToHandleExitValue();

    public Optional<IConvertStdoutToIData> getFunctionToHandleStdout();

    public Optional<IConvertByteArrayToIData> getFunctionToReadFromBytes();
}
