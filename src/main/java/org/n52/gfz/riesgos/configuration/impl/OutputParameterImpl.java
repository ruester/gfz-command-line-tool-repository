package org.n52.gfz.riesgos.configuration.impl;

import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OutputParameterImpl<T extends IData> implements IOutputParameter {

    private final String identifier;
    private final Class<T> bindingClass;
    private final boolean isOptional;
    private final String optionalAbstract;
    private final ICheckDataAndGetErrorMessage<T> validator;
    private final String path;
    private final IConvertByteArrayToIData<T> functionToHandleStderr;
    private final IConvertExitValueToIData<T> functionToHandleExitValue;
    private final IConvertByteArrayToIData<T> functionToHandleStdout;
    private final IReadIDataFromFiles<T> functionToReadFromFiles;
    private final List<String> supportedCRSForBBox;
    private final String schema;

    private OutputParameterImpl(final Builder<T> builder) {
        this.identifier = builder.identifier;
        this.bindingClass = builder.bindingClass;
        this.isOptional = builder.isOptional;
        this.optionalAbstract = builder.optionalAbstract;
        this.validator = builder.validator;
        this.path = builder.path;
        this.functionToHandleStderr = builder.functionToHandleStderr;
        this.functionToHandleExitValue = builder.functionToHandleExitValue;
        this.functionToHandleStdout = builder.functionToHandleStdout;
        this.functionToReadFromFiles = builder.functionToReadFromFiles;
        this.supportedCRSForBBox = builder.supportedCRSForBBox;
        this.schema = builder.schema;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Class<? extends IData> getBindingClass() {
        return bindingClass;
    }

    @Override
    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public Optional<String> getAbstract() {
        return Optional.ofNullable(optionalAbstract);
    }

    @Override
    public Optional<ICheckDataAndGetErrorMessage> getValidator() {
        return Optional.ofNullable(validator);
    }

    @Override
    public Optional<String> getPathToWriteToOrReadFromFile() {
        return Optional.ofNullable(path);
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
    public Optional<IReadIDataFromFiles> getFunctionToReadIDataFromFiles() {
        return Optional.ofNullable(functionToReadFromFiles);
    }

    @Override
    public Optional<List<String>> getSupportedCRSForBBox() {
        return Optional.ofNullable(supportedCRSForBBox);
    }

    @Override
    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OutputParameterImpl that = (OutputParameterImpl) o;
        return isOptional == that.isOptional &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(bindingClass, that.bindingClass) &&
                Objects.equals(optionalAbstract, that.optionalAbstract) &&
                Objects.equals(validator, that.validator) &&
                Objects.equals(path, that.path) &&
                Objects.equals(functionToHandleStderr, that.functionToHandleStderr) &&
                Objects.equals(functionToHandleExitValue, that.functionToHandleExitValue) &&
                Objects.equals(functionToHandleStdout, that.functionToHandleStdout) &&
                Objects.equals(functionToReadFromFiles, that.functionToReadFromFiles) &&
                Objects.equals(supportedCRSForBBox, that.supportedCRSForBBox) &&
                Objects.equals(schema, that.schema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, bindingClass, isOptional, optionalAbstract, validator, path, functionToHandleStderr, functionToHandleExitValue, functionToHandleStdout, functionToReadFromFiles, supportedCRSForBBox, schema);
    }

    public static class Builder<T extends IData> {
        private final String identifier;
        private final Class<T> bindingClass;
        private final boolean isOptional;
        private final String optionalAbstract;

        private ICheckDataAndGetErrorMessage<T> validator;
        private String path;
        private IConvertByteArrayToIData<T> functionToHandleStderr;
        private IConvertExitValueToIData<T> functionToHandleExitValue;
        private IConvertByteArrayToIData<T> functionToHandleStdout;
        private IReadIDataFromFiles<T> functionToReadFromFiles;
        private List<String> supportedCRSForBBox;
        private String schema;

        public Builder(
                final String aIdentifier,
                final Class<T> aBindingClass,
                final boolean aIsOptional,
                final String aOptionalAbstract
        ) {
            this.identifier = aIdentifier;
            this.bindingClass = aBindingClass;
            this.isOptional = aIsOptional;
            this.optionalAbstract = aOptionalAbstract;
        }

        public Builder withValidator(final ICheckDataAndGetErrorMessage<T> aValidator) {
            this.validator = aValidator;
            return this;
        }

        public Builder withPath(final String aPath) {
            this.path = aPath;
            return this;
        }

        public Builder withFunctionToHandleStderr(final IConvertByteArrayToIData<T> aFunctionToHandleStderr) {
            this.functionToHandleStderr = aFunctionToHandleStderr;
            return this;
        }

        public Builder withFunctionToHandleExitValue(final IConvertExitValueToIData<T> aFunctionToHandleExitValue) {
            this.functionToHandleExitValue = aFunctionToHandleExitValue;
            return this;
        }

        public Builder withFunctionToHandleStdout(final IConvertByteArrayToIData<T> aFunctionToHandleStdout) {
            this.functionToHandleStdout = aFunctionToHandleStdout;
            return this;
        }

        public Builder withFunctionToReadFromFiles(final IReadIDataFromFiles<T> aFunctionToReadFromFiles) {
            this.functionToReadFromFiles = aFunctionToReadFromFiles;
            return this;
        }

        public Builder withSchema(final String aSchema) {
            this.schema = aSchema;
            return this;
        }

        public Builder withSupportedCRSForBBox(final List<String> aSupportedCRSForBBox) {
            this.supportedCRSForBBox = aSupportedCRSForBBox;
            return this;
        }

        public IOutputParameter build() {
            return new OutputParameterImpl<>(this);
        }


    }
}
