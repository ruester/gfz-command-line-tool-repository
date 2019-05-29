package org.n52.gfz.riesgos.configuration.impl;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the identifier interface
 */
public class IdentifierWithBindingImpl implements IIdentifierWithBinding {

    private final String identifier;
    private final Class<? extends IData> bindingClass;
    private final ICheckDataAndGetErrorMessage validator;
    private final IConvertIDataToCommandLineParameter functionToTransformToCmd;
    private final String path;
    private final IWriteIDataToFiles functionToWriteToFiles;
    private final IConvertIDataToByteArray functionToWriteToStdin;
    private final IConvertByteArrayToIData functionToHandleStderr;
    private final IConvertExitValueToIData functionToHandleExitValue;
    private final IConvertByteArrayToIData functionToHandleStdout;
    private final IReadIDataFromFiles functionToReadFromFiles;
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
            final IWriteIDataToFiles functionToWriteToFiles,
            final IConvertIDataToByteArray functionToWriteToStdin,
            final IConvertByteArrayToIData functionToHandleStderr,
            final IConvertExitValueToIData functionToHandleExitValue,
            final IConvertByteArrayToIData functionToHandleStdout,
            final IReadIDataFromFiles functionToReadFromFiles,
            final List<String> allowedValues,
            final String defaultValue,
            final List<String> supportedCRSForBBox,
            final String schema) {
        this.identifier = identifier;
        this.bindingClass = bindingClass;
        this.validator = validator;
        this.functionToTransformToCmd = functionToTransformToCmd;
        this.path = path;
        this.functionToWriteToFiles = functionToWriteToFiles;
        this.functionToWriteToStdin = functionToWriteToStdin;
        this.functionToHandleStderr = functionToHandleStderr;
        this.functionToHandleExitValue = functionToHandleExitValue;
        this.functionToHandleStdout = functionToHandleStdout;
        this.functionToReadFromFiles = functionToReadFromFiles;
        this.allowedValues = allowedValues;
        this.defaultValue = defaultValue;
        this.supportedCRSForBBox = supportedCRSForBBox;
        this.schema = schema;
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
    public Optional<IWriteIDataToFiles> getFunctionToWriteIDataToFiles() {
        return Optional.ofNullable(functionToWriteToFiles);
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
    public Optional<IReadIDataFromFiles> getFunctionToReadIDataFromFiles() {
        return Optional.ofNullable(functionToReadFromFiles);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IdentifierWithBindingImpl that = (IdentifierWithBindingImpl) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(bindingClass, that.bindingClass) &&
                Objects.equals(validator, that.validator) &&
                Objects.equals(functionToTransformToCmd, that.functionToTransformToCmd) &&
                Objects.equals(path, that.path) &&
                Objects.equals(functionToWriteToFiles, that.functionToWriteToFiles) &&
                Objects.equals(functionToWriteToStdin, that.functionToWriteToStdin) &&
                Objects.equals(functionToHandleStderr, that.functionToHandleStderr) &&
                Objects.equals(functionToHandleExitValue, that.functionToHandleExitValue) &&
                Objects.equals(functionToHandleStdout, that.functionToHandleStdout) &&
                Objects.equals(functionToReadFromFiles, that.functionToReadFromFiles) &&
                Objects.equals(allowedValues, that.allowedValues) &&
                Objects.equals(defaultValue, that.defaultValue) &&
                Objects.equals(supportedCRSForBBox, that.supportedCRSForBBox) &&
                Objects.equals(schema, that.schema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, bindingClass, validator, functionToTransformToCmd, path, functionToWriteToFiles, functionToWriteToStdin, functionToHandleStderr, functionToHandleExitValue, functionToHandleStdout, functionToReadFromFiles, allowedValues, defaultValue, supportedCRSForBBox, schema);
    }

    /**
     * Builder for the Implementation
     */
    public static class Builder {
        private final String identifier;
        private final Class<? extends IData> bindingClass;

        private ICheckDataAndGetErrorMessage validator;
        private IConvertIDataToCommandLineParameter functionToTransformToCmd;
        private String path;
        private IWriteIDataToFiles functionToWriteToFiles;
        private IConvertIDataToByteArray functionToWriteToStdin;
        private IConvertByteArrayToIData functionToHandleStderr;
        private IConvertExitValueToIData functionToHandleExitValue;
        private IConvertByteArrayToIData functionToHandleStdout;
        private IReadIDataFromFiles functionToReadFromFiles;
        private List<String> allowedValues;
        private String defaultValue;
        private List<String> supportedCRSForBBox;
        private String schema;

        /**
         * Constructor
         * @param identifier identifier of the value
         * @param bindingClass binding class of the value
         */
        public Builder(final String identifier, final Class<? extends IData> bindingClass) {
            this.identifier = identifier;
            this.bindingClass = bindingClass;
        }

        /**
         * Set the validator
         * @param validator validator to check values
         * @return the same builder
         */
        public Builder withValidator(final ICheckDataAndGetErrorMessage validator) {
            this.validator = validator;
            return this;
        }

        /**
         * Set the function to transform the value to a command line argument
         * @param functionToTransformToCmd function that transforms a value to a string so it can be used as command
         *                                 line argument
         * @return the same builder
         */
        public Builder withFunctionToTransformToCmd(final IConvertIDataToCommandLineParameter functionToTransformToCmd) {
            this.functionToTransformToCmd = functionToTransformToCmd;
            return this;
        }

        /**
         * Set the path for a file
         * @param path path to read from or write to a file (relative to the working directory)
         * @return the same builder
         */
        public Builder withPath(final String path) {
            this.path = path;
            return this;
        }

        /**
         * Set the function to write the IData to files
         * @param functionToWriteToFiles function to write the IData to files
         * @return the same builder
         */
        public Builder withFunctionToWriteToFiles(final IWriteIDataToFiles functionToWriteToFiles) {
            this.functionToWriteToFiles = functionToWriteToFiles;
            return this;
        }

        /**
         * Set the function to transform the value to bytes for writing it to stdin
         * @param functionToWriteToStdin function to convert the content to byte array for writing to stdin
         * @return the same builder
         */
        public Builder withFunctionToWriteToStdin(final IConvertIDataToByteArray functionToWriteToStdin) {
            this.functionToWriteToStdin = functionToWriteToStdin;
            return this;
        }

        /**
         * Set the function to set the value based on byte array data from stderr
         * @param functionToHandleStderr function to read the value from stderr
         * @return the same builder
         */
        public Builder withFunctionToHandleStderr(final IConvertByteArrayToIData functionToHandleStderr) {
            this.functionToHandleStderr = functionToHandleStderr;
            return this;
        }

        /**
         * Set the function to set the value based on the integer exit value
         * @param functionToHandleExitValue function to read the value from exit value
         * @return the same builder
         */
        public Builder withFunctionToHandleExitValue(final IConvertExitValueToIData functionToHandleExitValue) {
            this.functionToHandleExitValue = functionToHandleExitValue;
            return this;
        }

        /**
         * Set the function to set the value based on byte array data from stdout
         * @param functionToHandleStdout function to read the value from stdout
         * @return the same builder
         */
        public Builder withFunctionToHandleStdout(final IConvertByteArrayToIData functionToHandleStdout) {
            this.functionToHandleStdout = functionToHandleStdout;
            return this;
        }

        /**
         * Set the function to read the value from files
         * @param functionToReadFromFiles function to read the value from files
         * @return the same builder
         */
        public Builder withFunctionToReadFromFiles(final IReadIDataFromFiles functionToReadFromFiles) {
            this.functionToReadFromFiles = functionToReadFromFiles;
            return this;
        }

        /**
         * Set the allowed values
         * @param allowedValues list with some values that the data can have
         * @return the same builder
         */
        public Builder withAllowedValues(final List<String> allowedValues) {
            this.allowedValues = allowedValues;
            return this;
        }

        /**
         * Set the default value
         * @param defaultValue default value of the data
         * @return the same builder
         */
        public Builder withDefaultValue(final String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Set the supported crs for bounding boxes
         * @param supportedCRSForBBox list with supported crs
         * @return the same builder
         */
        public Builder withSupportedCRSForBBox(final List<String> supportedCRSForBBox) {
            this.supportedCRSForBBox = supportedCRSForBBox;
            return this;
        }

        /**
         * Set the schema
         * @param schema schema for xml data
         * @return the same builder
         */
        public Builder withSchema(final String schema) {
            this.schema = schema;
            return this;
        }

        /**
         *
         * @return IdentifierWithBindingImpl
         */
        public IdentifierWithBindingImpl build() {
            return new IdentifierWithBindingImpl(identifier, bindingClass, validator,
                    functionToTransformToCmd, path, functionToWriteToFiles,
                    functionToWriteToStdin, functionToHandleStderr, functionToHandleExitValue,
                    functionToHandleStdout, functionToReadFromFiles, allowedValues,
                    defaultValue, supportedCRSForBBox, schema);
        }
    }
}
