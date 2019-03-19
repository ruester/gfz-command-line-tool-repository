package org.n52.gfz.riesgos.configuration.impl;

import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
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

public class IdentifierWithBindingImpl implements IIdentifierWithBinding {

    private final String identifier;
    private final Class<? extends IData> bindingClass;
    private final Optional<ICheckDataAndGetErrorMessage> validator;
    private final Optional<IConvertIDataToCommandLineParameter> functionToTransformToCmd;
    private final Optional<String> path;
    private final Optional<IConvertIDataToByteArray> functionToGetBytesToWrite;
    private final Optional<IWriteToStdin> functionToWriteToStdin;
    private final Optional<IConvertStderrToIData> functionToHandleStderr;
    private final Optional<IConvertExitValueToIData> functionToHandleExitValue;
    private final Optional<IConvertStdoutToIData> functionToHandleStdout;
    private final Optional<IConvertByteArrayToIData> functionToReadFromBytes;

    public IdentifierWithBindingImpl(
            final String identifier,
            final Class<? extends IData> bindingClass,
            final Optional<ICheckDataAndGetErrorMessage> validator,
            final Optional<IConvertIDataToCommandLineParameter> functionToTransformToCmd,
            final Optional<String> path,
            final Optional<IConvertIDataToByteArray> functionToGetBytesToWrite,
            final Optional<IWriteToStdin> functionToWriteToStdin,
            final Optional<IConvertStderrToIData> functionToHandleStderr,
            final Optional<IConvertExitValueToIData> functionToHandleExitValue,
            final Optional<IConvertStdoutToIData> functionToHandleStdout,
            final Optional<IConvertByteArrayToIData> functionToReadFromBytes) {
        this.identifier = identifier;
        this.bindingClass = bindingClass;
        this.validator = validator;
        this.functionToTransformToCmd = functionToTransformToCmd;
        this.path = path;
        this.functionToGetBytesToWrite = functionToGetBytesToWrite;
        this.functionToWriteToStdin = functionToWriteToStdin;
        this.functionToHandleStderr = functionToHandleStderr;
        this.functionToHandleExitValue = functionToHandleExitValue;
        this.functionToHandleStdout = functionToHandleStdout;
        this.functionToReadFromBytes = functionToReadFromBytes;
    }

    @Override
    public String getIdentifer() {
        return identifier;
    }

    @Override
    public Class<? extends IData> getBindingClass() {
        return bindingClass;
    }

    @Override
    public Optional<ICheckDataAndGetErrorMessage> getValidator() {
        return validator;
    }

    @Override
    public Optional<IConvertIDataToCommandLineParameter> getFunctionToTransformToCmd() {
        return functionToTransformToCmd;
    }

    @Override
    public Optional<String> getPathToWriteToOrReadFromFile() {
        return path;
    }

    @Override
    public Optional<IConvertIDataToByteArray> getFunctionToGetBytesToWrite() {
        return functionToGetBytesToWrite;
    }

    @Override
    public Optional<IWriteToStdin> getFunctionToWriteToStdin() {
        return functionToWriteToStdin;
    }

    @Override
    public Optional<IConvertStderrToIData> getFunctionToHandleStderr() {
        return functionToHandleStderr;
    }

    @Override
    public Optional<IConvertExitValueToIData> getFunctionToHandleExitValue() {
        return functionToHandleExitValue;
    }

    @Override
    public Optional<IConvertStdoutToIData> getFunctionToHandleStdout() {
        return functionToHandleStdout;
    }

    @Override
    public Optional<IConvertByteArrayToIData> getFunctionToReadFromBytes() {
        return functionToReadFromBytes;
    }
}
