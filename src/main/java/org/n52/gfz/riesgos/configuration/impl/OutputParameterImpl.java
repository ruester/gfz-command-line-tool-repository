package org.n52.gfz.riesgos.configuration.impl;

import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of the output identifier.
 * @param <T> Binding class of the output
 */
public final class OutputParameterImpl<T extends IData>
        implements IOutputParameter {

    private static final long serialVersionUID = -740693169389170577L;

    /**
     * Identifier of the ouput data.
     */
    private final String identifier;
    /**
     * Binding class of the output.
     */
    private final Class<T> bindingClass;
    /**
     * Flag to specify if the output is optional.
     */
    private final boolean isOptional;
    /**
     * Abstract of the output identifier.
     */
    private final String optionalAbstract;
    /**
     * Validator to check the output.
     */
    private final ICheckDataAndGetErrorMessage<T> validator;
    /**
     * Path to read the data from the filesystem (
     * relative to the working directory, inside of the
     * docker image).
     */
    private final String path;
    /**
     * Function to read the content from stderr.
     */
    private final IConvertByteArrayToIData<T> functionToHandleStderr;
    /**
     * Function to read the content from the exit value.
     */
    private final IConvertExitValueToIData<T> functionToHandleExitValue;
    /**
     * Function to read the content from stdout.
     */
    private final IConvertByteArrayToIData<T> functionToHandleStdout;
    /**
     * Function to read the content from files (with the given path).
     */
    private final IReadIDataFromFiles<T> functionToReadFromFiles;
    /**
     * List of supported crs in case of bounding box outputs.
     */
    private final List<String> supportedCRSForBBox;
    /**
     * Schema for xml files.
     */
    private final String schema;
    /**
     * Default format for the output (in case it has several possible
     * output formats, this will be the default one, so that
     * QGIS and owslib will take this one).
     */
    private final transient FormatEntry defaultFormat;

    /**
     * Private constructor. Use the builder.build() method
     * instead.
     * @param builder builder with the data.
     */
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
        this.defaultFormat = builder.defaultFormat;
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
    public Optional<FormatEntry> getDefaultFormat() {
        return Optional.ofNullable(defaultFormat);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OutputParameterImpl that = (OutputParameterImpl) o;
        return isOptional == that.isOptional
                && Objects.equals(identifier, that.identifier)
                && Objects.equals(bindingClass, that.bindingClass)
                && Objects.equals(optionalAbstract, that.optionalAbstract)
                && Objects.equals(validator, that.validator)
                && Objects.equals(path, that.path)
                && Objects.equals(functionToHandleStderr,
                    that.functionToHandleStderr)
                && Objects.equals(functionToHandleExitValue,
                    that.functionToHandleExitValue)
                && Objects.equals(functionToHandleStdout,
                    that.functionToHandleStdout)
                && Objects.equals(functionToReadFromFiles,
                    that.functionToReadFromFiles)
                && Objects.equals(supportedCRSForBBox, that.supportedCRSForBBox)
                && Objects.equals(schema, that.schema)
                && Objects.equals(defaultFormat, that.defaultFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, bindingClass, isOptional,
                optionalAbstract, validator, path, functionToHandleStderr,
                functionToHandleExitValue, functionToHandleStdout,
                functionToReadFromFiles, supportedCRSForBBox, schema,
                defaultFormat);
    }

    /**
     * Builder class to create the IOutputParameter.
     * @param <T> Binding class for the output.
     */
    public static class Builder<T extends IData> {
        /**
         * Identifier of the output.
         */
        private final String identifier;
        /**
         * Binding class of the output.
         */
        private final Class<T> bindingClass;
        /**
         * Flag to specify if the output is optional or not.
         */
        private final boolean isOptional;
        /**
         * Abstract of the output parameter.
         */
        private final String optionalAbstract;

        /**
         * Validator to check the output value.
         */
        private ICheckDataAndGetErrorMessage<T> validator;
        /**
         * Path to read the output from (inside of the docker image).
         */
        private String path;
        /**
         * Function to read the data from stderr.
         */
        private IConvertByteArrayToIData<T> functionToHandleStderr;
        /**
         * Function to read the data from the exit value.
         */
        private IConvertExitValueToIData<T> functionToHandleExitValue;
        /**
         * Function to read the data from stdout.
         */
        private IConvertByteArrayToIData<T> functionToHandleStdout;
        /**
         * Function to read the data from the files (with the path).
         */
        private IReadIDataFromFiles<T> functionToReadFromFiles;
        /**
         * List of supported crs (if the output is a bouding box).
         */
        private List<String> supportedCRSForBBox;
        /**
         * Schema if the output is xml.
         */
        private String schema;
        /**
         * Default format for the output.
         */
        private FormatEntry defaultFormat;

        /**
         * Creates the new builder.
         * @param aIdentifier identifier of the output parameter
         * @param aBindingClass binding class of the output parameter
         * @param aIsOptional flag if the output is optional or not
         * @param aOptionalAbstract abstract for the output
         */
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

        /**
         * Sets the validator.
         * @param aValidator object to check the data
         * @return this builder
         */
        public Builder withValidator(
                final ICheckDataAndGetErrorMessage<T> aValidator) {
            this.validator = aValidator;
            return this;
        }

        /**
         * Sets the path to read the data from.
         * @param aPath path to read the data from files
         * @return this builder
         */
        public Builder withPath(final String aPath) {
            this.path = aPath;
            return this;
        }

        /**
         * Sets the function to read from stderr.
         * @param aFunctionToHandleStderr function to read the data from stderr
         * @return this builder
         */
        public Builder withFunctionToHandleStderr(
                final IConvertByteArrayToIData<T> aFunctionToHandleStderr) {
            this.functionToHandleStderr = aFunctionToHandleStderr;
            return this;
        }

        /**
         * Sets the function to read the data from the exit value.
         * @param aFunctionToHandleExitValue function to read the data from
         *                                   the exit value
         * @return this builder
         */
        public Builder withFunctionToHandleExitValue(
                final IConvertExitValueToIData<T> aFunctionToHandleExitValue) {
            this.functionToHandleExitValue = aFunctionToHandleExitValue;
            return this;
        }

        /**
         * Sets the function to read the data from stdout.
         * @param aFunctionToHandleStdout function to read the data from stdout
         * @return this builder
         */
        public Builder withFunctionToHandleStdout(
                final IConvertByteArrayToIData<T> aFunctionToHandleStdout) {
            this.functionToHandleStdout = aFunctionToHandleStdout;
            return this;
        }

        /**
         * Sets the function to read the data from files (using the path).
         * @param aFunctionToReadFromFiles function to read the data from files.
         * @return this builder
         */
        public Builder withFunctionToReadFromFiles(
                final IReadIDataFromFiles<T> aFunctionToReadFromFiles) {
            this.functionToReadFromFiles = aFunctionToReadFromFiles;
            return this;
        }

        /**
         * Sets the schema (for xml).
         * @param aSchema schema for xml
         * @return this builder
         */
        public Builder withSchema(final String aSchema) {
            this.schema = aSchema;
            return this;
        }

        /**
         * Seths the supported crs if the data is a bouding box.
         * @param aSupportedCRSForBBox list with supported crs
         * @return this builder
         */
        public Builder withSupportedCRSForBBox(
                final List<String> aSupportedCRSForBBox) {
            this.supportedCRSForBBox = aSupportedCRSForBBox;
            return this;
        }

        /**
         * Seths the default format for the output.
         * @param aDefaultFormat default format for the output
         * @return this builder
         */
        public Builder withDefaultFormat(final FormatEntry aDefaultFormat) {
            this.defaultFormat = aDefaultFormat;
            return this;
        }

        /**
         * Creates the IOutputParameter.
         * @return OutputParameter
         */
        public IOutputParameter build() {
            return new OutputParameterImpl<>(this);
        }


    }
}
