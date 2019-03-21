package org.n52.gfz.riesgos.configuration.impl;

import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Optional;

public class IdentifierWithBindingImpl implements IIdentifierWithBinding {

    private final String identifier;
    private final Class<? extends IData> bindingClass;
    private final ICheckDataAndGetErrorMessage validator;
    private final IConvertIDataToCommandLineParameter functionToTransformToCmd;
    private final String path;
    private final IConvertIDataToByteArray functionToGetBytesToWrite;
    private final IConvertIDataToByteArray functionToWriteToStdin;
    private final IConvertByteArrayToIData functionToHandleStderr;
    private final IConvertExitValueToIData functionToHandleExitValue;
    private final IConvertByteArrayToIData functionToHandleStdout;
    private final IConvertByteArrayToIData functionToReadFromBytes;
    private final List<String> allowedValues;
    private final String defaultValue;
    private final List<String> supportedCRSForBBox;
    private final String schema;

    private IdentifierWithBindingImpl(
            final String identifier,
            final Class<? extends IData> bindingClass,
            final ICheckDataAndGetErrorMessage validator,
            final IConvertIDataToCommandLineParameter functionToTransformToCmd,
            final String path,
            final IConvertIDataToByteArray functionToGetBytesToWrite,
            final IConvertIDataToByteArray functionToWriteToStdin,
            final IConvertByteArrayToIData functionToHandleStderr,
            final IConvertExitValueToIData functionToHandleExitValue,
            final IConvertByteArrayToIData functionToHandleStdout,
            final IConvertByteArrayToIData functionToReadFromBytes,
            final List<String> allowedValues,
            final String defaultValue,
            final List<String> supportedCRSForBBox,
            final String schema) {
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
        this.allowedValues = allowedValues;
        this.defaultValue = defaultValue;
        this.supportedCRSForBBox = supportedCRSForBBox;
        this.schema = schema;
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
        return Optional.ofNullable(validator);
    }

    @Override
    public Optional<IConvertIDataToCommandLineParameter> getFunctionToTransformToCmd() {
        return Optional.ofNullable(functionToTransformToCmd);
    }

    @Override
    public Optional<String> getPathToWriteToOrReadFromFile() {
        return Optional.ofNullable(path);
    }

    @Override
    public Optional<IConvertIDataToByteArray> getFunctionToGetBytesToWrite() {
        return Optional.ofNullable(functionToGetBytesToWrite);
    }

    @Override
    public Optional<IConvertIDataToByteArray> getFunctionToWriteToStdin() {
        return Optional.ofNullable(functionToWriteToStdin);
    }

    @Override
    public Optional<IConvertByteArrayToIData> getFunctionToHandleStderr() {
        return Optional.ofNullable(functionToHandleStderr);
    }

    @Override
    public Optional<IConvertExitValueToIData> getFunctionToHandleExitValue() {
        return Optional.ofNullable(functionToHandleExitValue);
    }

    @Override
    public Optional<IConvertByteArrayToIData> getFunctionToHandleStdout() {
        return Optional.ofNullable(functionToHandleStdout);
    }

    @Override
    public Optional<IConvertByteArrayToIData> getFunctionToReadFromBytes() {
        return Optional.ofNullable(functionToReadFromBytes);
    }

    @Override
    public Optional<List<String>> getAllowedValues() {
        return Optional.ofNullable(allowedValues);
    }

    @Override
    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Override
    public Optional<List<String>> getSupportedCRSForBBox() {
        return Optional.ofNullable(supportedCRSForBBox);
    }

    @Override
    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }


    public static class Builder {
        private final String identifier;
        private final Class<? extends IData> bindingClass;

        private ICheckDataAndGetErrorMessage validator;
        private IConvertIDataToCommandLineParameter functionToTransformToCmd;
        private String path;
        private IConvertIDataToByteArray functionToGetBytesToWrite;
        private IConvertIDataToByteArray functionToWriteToStdin;
        private IConvertByteArrayToIData functionToHandleStderr;
        private IConvertExitValueToIData functionToHandleExitValue;
        private IConvertByteArrayToIData functionToHandleStdout;
        private IConvertByteArrayToIData functionToReadFromBytes;
        private List<String> allowedValues;
        private String defaultValue;
        private List<String> supportedCRSForBBox;
        private String schema;

        public Builder(final String identifier, final Class<? extends IData> bindingClass) {
            this.identifier = identifier;
            this.bindingClass = bindingClass;
        }

        public Builder withValidator(final ICheckDataAndGetErrorMessage validator) {
            this.validator = validator;
            return this;
        }

        public Builder withFunctionToTransformToCmd(final IConvertIDataToCommandLineParameter functionToTransformToCmd) {
            this.functionToTransformToCmd = functionToTransformToCmd;
            return this;
        }

        public Builder withPath(final String path) {
            this.path = path;
            return this;
        }

        public Builder withFunctionToGetBytesToWrite(final IConvertIDataToByteArray functionToGetBytesToWrite) {
            this.functionToGetBytesToWrite = functionToGetBytesToWrite;
            return this;
        }

        public Builder withFunctionToWriteToStdin(final IConvertIDataToByteArray functionToWriteToStdin) {
            this.functionToWriteToStdin = functionToWriteToStdin;
            return this;
        }

        public Builder withFunctionToHandleStderr(final IConvertByteArrayToIData functionToHandleStderr) {
            this.functionToHandleStderr = functionToHandleStderr;
            return this;
        }

        public Builder withFunctionToHandleExitValue(final IConvertExitValueToIData functionToHandleExitValue) {
            this.functionToHandleExitValue = functionToHandleExitValue;
            return this;
        }

        public Builder withFunctionToHandleStdout(final IConvertByteArrayToIData functionToHandleStdout) {
            this.functionToHandleStdout = functionToHandleStdout;
            return this;
        }

        public Builder withFunctionToReadFromBytes(final IConvertByteArrayToIData functionToReadFromBytes) {
            this.functionToReadFromBytes = functionToReadFromBytes;
            return this;
        }

        public Builder withAllowedValues(final List<String> allowedValues) {
            this.allowedValues = allowedValues;
            return this;
        }

        public Builder withDefaultValue(final String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder withSupportedCRSForBBox(final List<String> supportedCRSForBBox) {
            this.supportedCRSForBBox = supportedCRSForBBox;
            return this;
        }

        public Builder withSchema(final String schema) {
            this.schema = schema;
            return this;
        }

        public IdentifierWithBindingImpl build() {
            return new IdentifierWithBindingImpl(identifier, bindingClass, validator,
                    functionToTransformToCmd, path, functionToGetBytesToWrite,
                    functionToWriteToStdin, functionToHandleStderr, functionToHandleExitValue,
                    functionToHandleStdout, functionToReadFromBytes, allowedValues,
                    defaultValue, supportedCRSForBBox, schema);
        }
    }
}
