package org.n52.gfz.riesgos.configuration.impl;

import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoInputDescriptionType;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoOutputDescriptionType;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.wps.io.data.IData;

import java.util.Optional;

public class IdentifierWithBindingImpl implements IIdentifierWithBinding {

    private final String identifier;
    private final Class<? extends IData> bindingClass;
    private final Optional<ICheckDataAndGetErrorMessage> validator;
    private final Optional<IConvertIDataToCommandLineParameter> functionToTransformToCmd;
    private final Optional<String> path;
    private final Optional<IConvertIDataToByteArray> functionToGetBytesToWrite;
    private final Optional<IConvertIDataToByteArray> functionToWriteToStdin;
    private final Optional<IConvertByteArrayToIData> functionToHandleStderr;
    private final Optional<IConvertExitValueToIData> functionToHandleExitValue;
    private final Optional<IConvertByteArrayToIData> functionToHandleStdout;
    private final Optional<IConvertByteArrayToIData> functionToReadFromBytes;
    private final Optional<IAddTypeIntoInputDescriptionType> functionToAddTypeIntoInputDescriptionType;
    private final Optional<IAddTypeIntoOutputDescriptionType> functionToAddTypeIntoOutputDescriptionType;

    public IdentifierWithBindingImpl(
            final String identifier,
            final Class<? extends IData> bindingClass,
            final Optional<ICheckDataAndGetErrorMessage> validator,
            final Optional<IConvertIDataToCommandLineParameter> functionToTransformToCmd,
            final Optional<String> path,
            final Optional<IConvertIDataToByteArray> functionToGetBytesToWrite,
            final Optional<IConvertIDataToByteArray> functionToWriteToStdin,
            final Optional<IConvertByteArrayToIData> functionToHandleStderr,
            final Optional<IConvertExitValueToIData> functionToHandleExitValue,
            final Optional<IConvertByteArrayToIData> functionToHandleStdout,
            final Optional<IConvertByteArrayToIData> functionToReadFromBytes,
            final Optional<IAddTypeIntoInputDescriptionType> functionToAddTypeIntoInputDescriptionType,
            final Optional<IAddTypeIntoOutputDescriptionType> functionToAddTypeIntoOutputDescriptionType) {
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
        this.functionToAddTypeIntoInputDescriptionType = functionToAddTypeIntoInputDescriptionType;
        this.functionToAddTypeIntoOutputDescriptionType = functionToAddTypeIntoOutputDescriptionType;
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
    public Optional<IConvertIDataToByteArray> getFunctionToWriteToStdin() {
        return functionToWriteToStdin;
    }

    @Override
    public Optional<IConvertByteArrayToIData> getFunctionToHandleStderr() {
        return functionToHandleStderr;
    }

    @Override
    public Optional<IConvertExitValueToIData> getFunctionToHandleExitValue() {
        return functionToHandleExitValue;
    }

    @Override
    public Optional<IConvertByteArrayToIData> getFunctionToHandleStdout() {
        return functionToHandleStdout;
    }

    @Override
    public Optional<IConvertByteArrayToIData> getFunctionToReadFromBytes() {
        return functionToReadFromBytes;
    }

    @Override
    public Optional<IAddTypeIntoInputDescriptionType> getFunctionToAddInputDescriptionType() {
        return functionToAddTypeIntoInputDescriptionType;
    }

    @Override
    public Optional<IAddTypeIntoOutputDescriptionType> getFunctionToAddOutputDescriptionType() {
        return functionToAddTypeIntoOutputDescriptionType;
    }
}
